import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event/dist/cjs/index.js";
import { render, screen } from "@testing-library/vue";
import Tooltip from "primevue/tooltip";
import { describe, expect, it, vi } from "vitest";
import NormTranslationActionMenu from "~/components/documents/actionMenu/NormTranslationActionMenu.vue";

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
    href: "https://example.com/translations",
  });
});

function renderLiteratureActionMenu() {
  render(NormTranslationActionMenu, {
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
describe("NormTranslationActionMenu", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("renders all actions in correct order", async () => {
    renderLiteratureActionMenu();
    const menuitems = await screen.findAllByRole("menuitem");

    expect(menuitems).toHaveLength(3);
    expect(menuitems[0]).toHaveAccessibleName("Link to translation");
    expect(menuitems[1]).toHaveAccessibleName("Drucken");
    expect(menuitems[2]).toHaveAccessibleName("Als PDF speichern");
  });

  it("can copy link to currently viewed document", async () => {
    const user = userEvent.setup();
    renderLiteratureActionMenu();

    const copyButton = screen.getByRole("menuitem", {
      name: "Link to translation",
    });
    expect(copyButton).toBeVisible();
    expect(copyButton).toBeEnabled();

    await user.click(copyButton);

    expect(await navigator.clipboard.readText()).toEqual(
      "https://example.com/translations",
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
    renderLiteratureActionMenu();

    const printButton = screen.getByRole("menuitem", {
      name: "Drucken",
    });
    expect(printButton).toBeVisible();
    expect(printButton).toBeEnabled();

    await user.click(printButton);

    expect(globalThis.print).toHaveBeenCalledOnce();
  });

  it("renders disabled PDF button", async () => {
    renderLiteratureActionMenu();

    const pdfButton = screen.getByRole("menuitem", {
      name: "Als PDF speichern",
    });
    expect(pdfButton).toBeVisible();
    expect(pdfButton).toBeDisabled();
  });
});
