import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event/dist/cjs/index.js";
import { render, screen } from "@testing-library/vue";
import Tooltip from "primevue/tooltip";
import { describe, expect, it, vi } from "vitest";
import NormActionMenu from "~/components/documents/actionMenu/NormActionMenu.vue";
import type { LegislationManifestation, LegislationExpression } from "~/types";

const { mockToastAdd } = vi.hoisted(() => ({
  mockToastAdd: vi.fn(),
}));

vi.mock("~/composables/useBackendUrl", () => ({
  default: vi.fn((url?: string) => url),
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
  encoding: [
    {
      encodingFormat: "application/xml",
      contentUrl: "https://example.com/v1/xml-content",
    } as LegislationManifestation,
  ],
} as LegislationExpression;

function renderNormActionMenu(
  metadata: LegislationExpression = mockLegislationWork,
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

// NOTE: only testing the "desktop" variant here as testing the different
// variants which are based on the screen size is not reliably doable
// without a real browser - desktop and mobile will be tested in the e2e tests
describe("NormActionMenu", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("renders all actions in correct order", async () => {
    renderNormActionMenu();
    const menuitems = await screen.findAllByRole("menuitem");

    expect(menuitems).toHaveLength(5);
    expect(menuitems[0]).toHaveAccessibleName(
      "Link zur jeweils gültigen Fassung kopieren",
    );
    expect(menuitems[1]).toHaveAccessibleName(
      "Permalink zu dieser Fassung kopieren",
    );
    expect(menuitems[2]).toHaveAccessibleName("Drucken");
    expect(menuitems[3]).toHaveAccessibleName("Als PDF speichern");
    expect(menuitems[4]).toHaveAccessibleName("XML anzeigen");
  });

  it("can copy link to currently valid expression", async () => {
    const user = userEvent.setup();
    renderNormActionMenu();

    const copyButton = screen.getByRole("menuitem", {
      name: "Link zur jeweils gültigen Fassung kopieren",
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

    const copyButton = screen.getByRole("menuitem", {
      name: "Permalink zu dieser Fassung kopieren",
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

    const printButton = screen.getByRole("menuitem", {
      name: "Drucken",
    });
    expect(printButton).toBeVisible();
    expect(printButton).toBeEnabled();

    await user.click(printButton);

    expect(globalThis.print).toHaveBeenCalledOnce();
  });

  it("renders disabled PDF button", async () => {
    renderNormActionMenu();

    const pdfButton = screen.getByRole("menuitem", {
      name: "Als PDF speichern",
    });
    expect(pdfButton).toBeVisible();
    expect(pdfButton).toBeDisabled();
  });

  it("can open link to xml view", async () => {
    renderNormActionMenu();

    const xmlLink = screen.getByRole("menuitem", {
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

    const menuitems = await screen.findAllByRole("menuitem");
    expect(menuitems).toHaveLength(5);

    const translationLink = screen.queryByRole("menuitem", {
      name: "Zur englischen Übersetzung",
    });

    expect(translationLink).not.toBeInTheDocument();
  });

  it("renders translation link button if translation url given", async () => {
    renderNormActionMenu(mockLegislationWork, "/translations/test");

    const menuitems = await screen.findAllByRole("menuitem");
    expect(menuitems).toHaveLength(6);

    const translationLink = screen.getByRole("menuitem", {
      name: "Zur englischen Übersetzung",
    });
    expect(menuitems[5]).toEqual(translationLink);
    expect(translationLink).toBeVisible();
    expect(translationLink).toBeEnabled();
    expect(translationLink).toHaveAttribute("href", "/translations/test");
  });
});
