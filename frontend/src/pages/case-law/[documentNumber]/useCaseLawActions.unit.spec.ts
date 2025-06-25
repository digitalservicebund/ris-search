import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { useCaseLawActions } from "./useCaseLawActions";
import type { CaseLaw } from "~/types";

const { mockNavigateTo } = vi.hoisted(() => ({
  mockNavigateTo: vi.fn(),
}));

mockNuxtImport("navigateTo", () => mockNavigateTo);

vi.mock("~/utils/config", () => ({
  isPrototypeProfile: vi.fn(),
}));

vi.mock("~/utils/caseLawUtils", () => ({
  getEncodingURL: vi.fn(),
}));

describe("useCaseLawActions", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("window", {
      print: vi.fn(),
    });
  });

  it("should include link and PDF actions by default", () => {
    vi.mocked(isPrototypeProfile).mockReturnValue(false);
    vi.mocked(getEncodingURL).mockReturnValue(undefined);

    const caseLaw = ref({} as CaseLaw);
    const { actions } = useCaseLawActions(caseLaw);

    const keys = actions.value.map((value) => value.key);

    expect(keys).toEqual(["link", "pdf"]);
  });

  it("should disable PDF button if isPrototypeProfile returns true", () => {
    vi.mocked(isPrototypeProfile).mockReturnValue(true); // Disables PDF button

    const caseLaw = ref({} as CaseLaw);
    const { actions } = useCaseLawActions(caseLaw);

    const pdfAction = actions.value.find((a) => a.key === "pdf");
    expect(pdfAction).toBeDefined();
    expect(pdfAction?.disabled).toBe(true);
  });

  it("should include XML action if xmlUrl is available", async () => {
    vi.mocked(getEncodingURL).mockReturnValue("encodingURL");

    const caseLaw = ref({} as CaseLaw);
    const { actions } = useCaseLawActions(caseLaw);

    const xmlAction = actions.value.find((a) => a.key === "xml");
    expect(xmlAction).toBeDefined();
    expect(xmlAction?.url).toBe("encodingURL");

    await xmlAction?.command?.();
    expect(navigateTo).toHaveBeenCalledExactlyOnceWith("encodingURL", {
      external: true,
    });
  });

  it("should call window.print when PDF action command is executed", () => {
    vi.mocked(isPrototypeProfile).mockReturnValue(false); // Enable PDF button
    const caseLaw = ref({} as CaseLaw);
    const { actions } = useCaseLawActions(caseLaw);

    const pdfAction = actions.value.find((a) => a.key === "pdf");
    pdfAction?.command?.();

    expect(window.print).toHaveBeenCalledOnce();
  });
});
