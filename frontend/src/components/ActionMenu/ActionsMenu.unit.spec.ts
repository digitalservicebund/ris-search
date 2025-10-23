import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { type DOMWrapper, mount, type VueWrapper } from "@vue/test-utils";
import Menu from "primevue/menu";
import { beforeEach, vi } from "vitest";
import ActionsMenu from "./ActionsMenu.vue";
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

describe("ActionsMenu.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("navigator", {
      clipboard: {
        writeText: vi.fn(),
      },
    });
  });

  it("it passes props to createActionMenuItems to create items", () => {
    const spy = vi.spyOn(actionMenuUtils, "createActionMenuItems");

    const props = {
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    };

    mount(ActionsMenu, {
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

    mount(ActionsMenu, {
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

  it("can navigate to an url", async () => {
    const spy = vi.spyOn(actionMenuUtils, "createActionMenuItems");

    mount(ActionsMenu, {
      global: {
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(spy).toHaveBeenCalledTimes(1);

    const navigationCommand = spy.mock.calls[0]?.[2] as (
      url: string,
    ) => Promise<void>;

    const navigationUrl = "https://navigation.com";
    await navigationCommand(navigationUrl);

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(navigationUrl, {
      external: true,
    });
  });

  it("shows small or large ActionMenu depending on screen size", () => {
    const wrapper = mount(ActionsMenu, {
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
    const wrapper = mount(ActionsMenu, {
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
    const wrapper = mount(ActionsMenu, {
      global: {
        stubs: {
          teleport: true,
        },
        directives: {
          tooltip: () => {},
        },
      },
    });

    expect(wrapper.findComponent(Menu).isVisible()).toBe(false);
    await toggleMenu(wrapper);
    expect(wrapper.findComponent(Menu).isVisible()).toBe(true);
  });

  it("correctly renders action items on small screen", async () => {
    const wrapper = mount(ActionsMenu, {
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
