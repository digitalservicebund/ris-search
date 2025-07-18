import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useNormActions } from "./useNormActions";
import type { LegislationWork } from "~/types";

const { mockToastAdd, mockNavigateTo } = vi.hoisted(() => ({
  mockToastAdd: vi.fn(),
  mockNavigateTo: vi.fn(),
}));

vi.mock("primevue/usetoast", () => ({
  useToast: () => ({
    add: mockToastAdd,
  }),
}));

mockNuxtImport("navigateTo", () => mockNavigateTo);

vi.mock("~/utils/config", () => ({
  isPrototypeProfile: vi.fn(),
}));

vi.mock("~/utils/normUtils", () => ({
  getManifestationUrl: vi.fn(),
}));

describe("useNormActions", () => {
  const normsBaseUrl = "https://legislation.example.com/";
  const mockLegislationWork = {
    legislationIdentifier: "eli/bgbl-test/etc",
  } as LegislationWork;

  const workUrl = normsBaseUrl + mockLegislationWork.legislationIdentifier;
  const expressionUrl = workUrl + "/expression";
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("window", {
      location: {
        href: expressionUrl,
      },
      print: vi.fn(),
    });
    vi.stubGlobal("navigator", {
      clipboard: {
        writeText: vi.fn(),
      },
    });
  });

  it("should include link, permalink, print and PDF actions by default", () => {
    vi.mocked(getManifestationUrl).mockReturnValue(undefined);

    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    const keys = actions.value.map((value) => value.key);

    expect(keys).toEqual(["link", "permalink", "print", "pdf"]);
  });

  it("should disable PDF button", () => {
    vi.mocked(isPrototypeProfile).mockReturnValue(true); // Disable PDF button

    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    const pdfAction = actions.value.find((a) => a.key === "pdf");
    expect(pdfAction).toBeDefined();
    expect(pdfAction?.disabled).toBe(true);
  });

  it("should include XML action if xmlUrl is available", async () => {
    const mockXmlUrl = expressionUrl + "/manifestation/xml";
    vi.mocked(getManifestationUrl).mockReturnValue(mockXmlUrl);

    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    const xmlAction = actions.value.find((a) => a.key === "xml");
    expect(xmlAction).toBeDefined();
    expect(xmlAction?.url).toBe(mockXmlUrl);

    await xmlAction?.command?.();
    expect(navigateTo).toHaveBeenCalledExactlyOnceWith(mockXmlUrl, {
      external: true,
    });
  });

  it("should call window.print when print action command is executed", () => {
    vi.mocked(isPrototypeProfile).mockReturnValue(false); // Enable PDF button
    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    const printAction = actions.value.find((a) => a.key === "print");
    printAction?.command?.();

    expect(window.print).toHaveBeenCalledOnce();
  });

  it("should copy work URL and show success message when link action command is executed", async () => {
    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    // Ensure workUrl is calculated correctly based on the mock window.location.href and metadata
    const linkAction = actions.value.find((a) => a.key === "link");
    await linkAction?.command?.();

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      workUrl,
    );
    expect(mockToastAdd).toHaveBeenCalledOnce();
  });

  it("should copy current URL and show success message when permalink action command is executed", async () => {
    const metadata = ref(mockLegislationWork);
    const { actions } = useNormActions(metadata);

    const permalinkAction = actions.value.find((a) => a.key === "permalink");
    expect(permalinkAction?.command).toBeDefined();
    await permalinkAction?.command?.();

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      expressionUrl,
    );
    expect(mockToastAdd).toHaveBeenCalledOnce();
  });
});
