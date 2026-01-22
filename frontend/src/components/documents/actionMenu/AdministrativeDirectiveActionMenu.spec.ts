import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event/dist/cjs/index.js";
import { render, screen } from "@testing-library/vue";
import Tooltip from "primevue/tooltip";
import { describe, expect, it, vi } from "vitest";
import AdministrativeDirectiveActionMenu from "~/components/documents/actionMenu/AdministrativeDirectiveActionMenu.vue";
import type { AdministrativeDirective } from "~/types";

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
    href: "https://example.com/administrative-directive",
  });
});

const mockedAdministrativeDirective = {
  encoding: [
    {
      contentUrl:
        "https://example.com/v1/administrative-directive/XXAD000000001.xml",
      encodingFormat: "application/xml",
    },
  ],
} as AdministrativeDirective;

function renderAdministrativeDirectiveActionMenu() {
  render(AdministrativeDirectiveActionMenu, {
    props: {
      administrativeDirective: mockedAdministrativeDirective,
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
describe("LiteratureActionMenu", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("renders all actions in correct order", async () => {
    renderAdministrativeDirectiveActionMenu();
    const menuitems = await screen.findAllByRole("menuitem");

    expect(menuitems).toHaveLength(4);
    expect(menuitems[0]).toHaveAccessibleName("Link kopieren");
    expect(menuitems[1]).toHaveAccessibleName("Drucken");
    expect(menuitems[2]).toHaveAccessibleName("Als PDF speichern");
    expect(menuitems[3]).toHaveAccessibleName("XML anzeigen");
  });

  it("can copy link to currently viewed document", async () => {
    const user = userEvent.setup();
    renderAdministrativeDirectiveActionMenu();

    const copyButton = screen.getByRole("menuitem", {
      name: "Link kopieren",
    });
    expect(copyButton).toBeVisible();
    expect(copyButton).toBeEnabled();

    await user.click(copyButton);

    expect(await navigator.clipboard.readText()).toEqual(
      "https://example.com/administrative-directive",
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
    renderAdministrativeDirectiveActionMenu();

    const printButton = screen.getByRole("menuitem", {
      name: "Drucken",
    });
    expect(printButton).toBeVisible();
    expect(printButton).toBeEnabled();

    await user.click(printButton);

    expect(globalThis.print).toHaveBeenCalledOnce();
  });

  it("renders disabled PDF button", async () => {
    renderAdministrativeDirectiveActionMenu();

    const pdfButton = screen.getByRole("menuitem", {
      name: "Als PDF speichern",
    });
    expect(pdfButton).toBeVisible();
    expect(pdfButton).toBeDisabled();
  });

  it("can open link to xml view", async () => {
    renderAdministrativeDirectiveActionMenu();

    const xmlLink = screen.getByRole("menuitem", {
      name: "XML anzeigen",
    });
    expect(xmlLink).toBeVisible();
    expect(xmlLink).toBeEnabled();
    expect(xmlLink).toHaveAttribute(
      "href",
      "https://example.com/v1/administrative-directive/XXAD000000001.xml",
    );
  });
});
