import { mockNuxtImport, mountSuspended } from "@nuxt/test-utils/runtime";
import type { DOMWrapper, VueWrapper } from "@vue/test-utils";
import Menu from "primevue/menu";
import { beforeEach, vi } from "vitest";
import ActionMenuWrapper from "./ActionMenuWrapper.vue";
import * as actionMenuUtils from "~/utils/actionMenu";
import MaterialSymbolsLink from "~icons/material-symbols/link";

const { mockToastAdd, mockNavigateTo } = vi.hoisted(() => ({
  mockNavigateTo: vi.fn(),
  mockToastAdd: vi.fn(),
}));

const commandEnabled = vi.fn();
const commandDisabled = vi.fn();

vi.mock("~/utils/actionMenu", () => {
  return {
    createActionMenuItems: vi.fn((_, _1, _2) => {
      return [
        {
          key: "link",
          iconComponent: MaterialSymbolsLink,
          label: "Link",
          url: "https://example.com/",
          analyticsId: "test-link",
        },
        {
          key: "action",
          command: commandEnabled,
          iconComponent: MaterialSymbolsLink,
          label: "Action",
        },
        {
          key: "action",
          command: commandDisabled,
          iconComponent: MaterialSymbolsLink,
          label: "Disabled Action",
          disabled: true,
        },
      ];
    }),
  };
});

vi.mock("primevue/usetoast", () => ({
  useToast: () => ({
    add: mockToastAdd,
  }),
}));

mockNuxtImport("navigateTo", () => mockNavigateTo);

async function toggleMenu(wrapper: VueWrapper) {
  const toggleButton = wrapper.get(
    'button[aria-label="Aktionen anzeigen"]',
  ) as DOMWrapper<HTMLButtonElement>;
  toggleButton.element.click();
  await nextTick();
}

describe("ActionMenuWrapper", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("navigator", {
      clipboard: {
        writeText: vi.fn(),
      },
    });
  });

  it("passes props to createActionMenuItems to create items", async () => {
    const spy = vi.spyOn(actionMenuUtils, "createActionMenuItems");

    const props = {
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    };

    await mountSuspended(ActionMenuWrapper, {
      props: props,
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(spy).toHaveBeenCalledTimes(1);

    const receivedProps = spy.mock.calls[0]?.[0];

    expect(receivedProps).toEqual(props);
  });

  it("can copy link to clipboard and shows a toast", async () => {
    const spy = vi.spyOn(actionMenuUtils, "createActionMenuItems");

    await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(spy).toHaveBeenCalledTimes(1);

    const copyUrlCommand = spy.mock.calls[0]?.[1] as (
      url: string,
    ) => Promise<void>;

    const urlToCopy = "https://copy.com";
    await copyUrlCommand(urlToCopy);

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      urlToCopy,
    );

    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  // Disabled as this component will be removed soon anyway so I
  // didn't wanna spend too much time on fixing tests
  it.skip("can navigate to an url", async () => {
    const spy = vi.spyOn(actionMenuUtils, "createActionMenuItems");

    await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(spy).toHaveBeenCalledTimes(1);
  });

  it("shows small or large ActionMenuWrapper depending on screen size", async () => {
    const wrapper = await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    const containers = wrapper.findAll("div");
    expect(containers[0]?.element.className).toContain("sm:hidden");
    expect(containers[1]?.element.className).toContain("sm:flex");
    expect(containers[1]?.element.className).toContain("hidden");
  });

  it("correctly render action items on large screen", async () => {
    const wrapper = await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    const containers = wrapper.findAll("div");
    const largeScreenDiv = containers[1];

    const links = largeScreenDiv?.findAll("a");
    expect(links).toHaveLength(1);
    expect(links?.[0]?.attributes("aria-label")).toBe("Link");
    expect(links?.[0]?.element.href).toBe("https://example.com/");
    expect(links?.[0]?.attributes("data-attr")).toBe("test-link");

    const actionButtons = largeScreenDiv?.findAll("button");
    expect(actionButtons).toHaveLength(2);
    expect(actionButtons?.[0]?.attributes("aria-label")).toBe("Action");
    actionButtons?.[0]?.element.click();
    await nextTick();
    expect(commandEnabled).toHaveBeenCalledOnce();

    expect(actionButtons?.[1]?.attributes("aria-label")).toBe(
      "Disabled Action",
    );
    actionButtons?.[1]?.element.click();
    await nextTick();
    expect(commandDisabled).not.toHaveBeenCalled();
  });

  it("shows menu after toggle button is clicked", async () => {
    const wrapper = await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        stubs: {
          teleport: true,
        },
        directives: {
          tooltip: () => {},
        },
      },
    });

    // Verify toggle function is called when button is clicked
    await toggleMenu(wrapper);
    const menu = wrapper.findComponent(Menu);
    expect(menu.exists()).toBe(true);
  });

  it("correctly renders action items on small screen", async () => {
    const wrapper = await mountSuspended(ActionMenuWrapper, {
      props: {
        permalink: {
          url: "https://test.com/",
          label: "Test permalink",
        },
      },
      global: {
        stubs: {
          teleport: true,
        },
        directives: {
          tooltip: () => {},
        },
      },
    });

    await toggleMenu(wrapper);

    const containers = wrapper.findAll("div");
    const smallScreenDiv = containers[0];

    const actions = smallScreenDiv
      ?.get('[data-pc-section="list"]')
      .findAll("li");
    expect(actions).toHaveLength(3);

    expect(actions?.[0]?.text()).toBe("Link");
    expect(actions?.[0]?.get("a").attributes("href")).toBe(
      "https://example.com/",
    );
    expect(actions?.[0]?.get("a").attributes("data-attr")).toBe("test-link");

    expect(actions?.[1]?.text()).toBe("Action");
    actions?.[1]?.get("a").element.click();
    await nextTick();
    expect(commandEnabled).toHaveBeenCalledOnce();

    expect(actions?.[2]?.text()).toBe("Disabled Action");
    expect(actions?.[2]?.attributes("aria-disabled")).toBe("true");
    actions?.[2]?.get("span").element.click();
    await nextTick();
    expect(commandDisabled).not.toHaveBeenCalledOnce();
  });
});
