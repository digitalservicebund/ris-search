import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import userEvent from "@testing-library/user-event/dist/cjs/index.js";
import { render, screen } from "@testing-library/vue";
import Tooltip from "primevue/tooltip";
import { describe, expect, it, vi } from "vitest";
import NormActionMenu from "~/components/documents/actionMenu/NormActionMenu.vue";
import type {
  LegislationExpression,
  LegislationManifestation,
  LegislationWork,
} from "~/types";

const { mockToastAdd } = vi.hoisted(() => ({
  mockToastAdd: vi.fn(),
}));

vi.mock("primevue/usetoast", () => ({
  useToast: () => ({
    add: mockToastAdd,
  }),
}));

mockNuxtImport("useRequestURL", () => {
  return () => ({
    href: "https://example.com/eli/foo",
  });
});

const mockLegislationWork = {
  legislationIdentifier: "eli/bgbl-test",
  workExample: {
    encoding: [
      {
        encodingFormat: "application/xml",
        contentUrl: "https://example.com/v1/xml-content",
      } as LegislationManifestation,
    ],
  } as LegislationExpression,
} as LegislationWork;

function renderNormActionMenu(
  metadata: LegislationWork = mockLegislationWork,
  translationUrl?: string,
) {
  render(NormActionMenu, {
    props: {
      metadata: metadata,
      translationUrl: translationUrl,
    },
    global: {
      directives: { tooltip: Tooltip },
      stubs: {
        NuxtLink: {
          template: '<a :href="to"><slot /></a>',
          props: ["to"],
        },
      },
    },
  });
}

describe("NormActionMenu", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("can copy link to currently valid expression", async () => {
    const user = userEvent.setup();
    renderNormActionMenu();

    const copyButton = screen.getByRole("button", {
      name: "Link zur jeweils gültigen Fassung",
    });
    expect(copyButton).toBeVisible();
    expect(copyButton).toBeEnabled();

    await user.click(copyButton);

    expect(await navigator.clipboard.readText()).toEqual(
      "https://example.com/eli/bgbl-test",
    );

    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  it("can copy link to the expression currently viewed", async () => {
    const user = userEvent.setup();
    renderNormActionMenu();

    const copyButton = screen.getByRole("button", {
      name: "Permalink zu dieser Fassung",
    });
    expect(copyButton).toBeVisible();
    expect(copyButton).toBeEnabled();

    await user.click(copyButton);

    expect(await navigator.clipboard.readText()).toEqual(
      "https://example.com/eli/foo",
    );

    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  it("can open the print dialog", async () => {
    globalThis.print = vi.fn();
    const user = userEvent.setup();
    renderNormActionMenu();

    const printButton = screen.getByRole("button", {
      name: "Drucken",
    });
    expect(printButton).toBeVisible();
    expect(printButton).toBeEnabled();

    await user.click(printButton);

    expect(globalThis.print).toHaveBeenCalledOnce();
  });

  it("renders disabled PDF button", async () => {
    renderNormActionMenu();

    const pdfButton = screen.getByRole("button", {
      name: "Als PDF speichern",
    });
    expect(pdfButton).toBeVisible();
    expect(pdfButton).toBeDisabled();
  });

  it("can open link to xml view", async () => {
    renderNormActionMenu();

    const xmlLink = screen.getByRole("link", {
      name: "XML anzeigen",
    });
    expect(xmlLink).toBeVisible();
    expect(xmlLink).toBeEnabled();
    expect(xmlLink).toHaveAttribute(
      "href",
      "https://example.com/v1/xml-content",
    );
  });

  it("does not show translation link button if no translation url given", async () => {
    renderNormActionMenu();

    const translationLink = screen.queryByRole("link", {
      name: "Zur englischen Übersetzung",
    });

    expect(translationLink).not.toBeInTheDocument();
  });

  it("renders translation link button if translation url given", async () => {
    renderNormActionMenu(mockLegislationWork, "/translations/test");

    const translationLink = screen.getByRole("link", {
      name: "Zur englischen Übersetzung",
    });
    expect(translationLink).toBeVisible();
    expect(translationLink).toBeEnabled();
    expect(translationLink).toHaveAttribute("href", "/translations/test");
  });
});
