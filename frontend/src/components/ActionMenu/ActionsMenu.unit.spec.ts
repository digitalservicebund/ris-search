import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import type { VueWrapper } from "@vue/test-utils";
import { shallowMount } from "@vue/test-utils";
import { beforeEach, vi } from "vitest";
import ActionsMenu from "./ActionsMenu.vue";
import type { ActionsMenuProps } from "~/components/ActionMenu/ActionsMenu.vue";
import LargeActionsMenu from "~/components/ActionMenu/LargeActionsMenu.vue";
import SmallActionsMenu from "~/components/ActionMenu/SmallActionsMenu.vue";
import PDFIcon from "~/components/icons/PDFIcon.vue";
import UpdatingLinkIcon from "~/components/icons/UpdatingLinkIcon.vue";
import XMLIcon from "~/components/icons/XMLIcon.vue";
import MaterialSymbolsLink from "~icons/material-symbols/link";
import MaterialSymbolsPrint from "~icons/material-symbols/print";

const { mockToastAdd, mockNavigateTo } = vi.hoisted(() => ({
  mockNavigateTo: vi.fn(),
  mockToastAdd: vi.fn(),
}));

vi.mock("primevue/usetoast", () => ({
  useToast: () => ({
    add: mockToastAdd,
  }),
}));

mockNuxtImport("navigateTo", () => mockNavigateTo);

const minimalExpectedActions = [
  {
    key: "permalink",
    label: "Link kopieren",
    iconComponent: MaterialSymbolsLink,
    url: "https://permalink.com/",
    disabled: true,
  },
  {
    key: "print",
    label: "Drucken",
    iconComponent: MaterialSymbolsPrint,
  },
  {
    key: "pdf",
    label: "Als PDF speichern",
    iconComponent: PDFIcon,
    disabled: true,
  },
];

function shallowMountWithProps(props: ActionsMenuProps): VueWrapper {
  return shallowMount(ActionsMenu, {
    props: props,
    global: {
      directives: {
        tooltip: () => {},
      },
    },
  });
}

function findCommandForActionWithKey(wrapper: VueWrapper, key: string) {
  const largeActionsMenu = wrapper.findComponent(LargeActionsMenu);
  return largeActionsMenu.props().actions.find((action) => action.key === key)
    ?.command;
}

describe("ActionsMenu.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("window", {
      print: vi.fn(),
    });
    vi.stubGlobal("navigator", {
      clipboard: {
        writeText: vi.fn(),
      },
    });
  });

  it("shows small or large ActionMenu depending on screen size", () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        url: "https://permalink.com/",
        label: "Copy permalink",
      },
    });

    const containers = wrapper.findAll("div");
    expect(containers[0].element.className).toContain("sm:hidden");
    expect(containers[0].findComponent(SmallActionsMenu).exists()).toBe(true);
    expect(containers[1].element.className).toContain("sm:flex");
    expect(containers[1].element.className).toContain("hidden");
    expect(containers[1].findComponent(LargeActionsMenu).exists()).toBe(true);
  });

  it("it passes correct props if only permalink is given", () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    });

    const expectedActions = {
      actions: minimalExpectedActions,
    };

    const largeActionsMenu = wrapper.findComponent(LargeActionsMenu);
    const smallActionsMenu = wrapper.findComponent(SmallActionsMenu);

    expect(largeActionsMenu.props()).toMatchObject(expectedActions);
    expect(smallActionsMenu.props()).toMatchObject(expectedActions);
  });

  it("prepends link action if given", () => {
    const wrapper = shallowMountWithProps({
      link: {
        url: "https://link.com/",
        label: "Copy link",
      },
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    });

    const expectedActions = {
      actions: [
        {
          key: "link",
          label: "Copy link",
          iconComponent: UpdatingLinkIcon,
          url: "https://link.com/",
        },
        ...minimalExpectedActions,
      ],
    };

    const largeActionsMenu = wrapper.findComponent(LargeActionsMenu);
    const smallActionsMenu = wrapper.findComponent(SmallActionsMenu);

    expect(largeActionsMenu.props()).toMatchObject(expectedActions);
    expect(smallActionsMenu.props()).toMatchObject(expectedActions);
  });

  it("appends xml action if given", () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
      xmlUrl: "https://xml.xml",
    });

    const expectedActions = {
      actions: [
        ...minimalExpectedActions,
        {
          key: "xml",
          label: "XML anzeigen",
          iconComponent: XMLIcon,
          url: "https://xml.xml",
          dataAttribute: "xml-view",
        },
      ],
    };

    const largeActionsMenu = wrapper.findComponent(LargeActionsMenu);
    const smallActionsMenu = wrapper.findComponent(SmallActionsMenu);

    expect(largeActionsMenu.props()).toMatchObject(expectedActions);
    expect(smallActionsMenu.props()).toMatchObject(expectedActions);
  });

  it("can copy link to clipboard and shows a toast", async () => {
    const wrapper = shallowMountWithProps({
      link: {
        url: "https://link.com/",
        label: "Copy link",
      },
      permalink: {
        label: "Foo",
      },
    });

    const command = findCommandForActionWithKey(wrapper, "link");
    await command?.();

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      "https://link.com/",
    );

    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  it("can copy permalink to clipboard and shows a toast", async () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    });

    const command = findCommandForActionWithKey(wrapper, "permalink");
    await command?.();

    expect(navigator.clipboard.writeText).toHaveBeenCalledExactlyOnceWith(
      "https://permalink.com/",
    );
    expect(mockToastAdd).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        summary: "Kopiert!",
      }),
    );
  });

  it("can start print dialog", () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        url: "https://permalink.com/",
        label: "Link kopieren",
        disabled: true,
      },
    });

    const command = findCommandForActionWithKey(wrapper, "print");
    command?.();

    expect(window.print).toHaveBeenCalled();
  });

  it("can navigate to xml", async () => {
    const wrapper = shallowMountWithProps({
      permalink: {
        label: "Foo",
      },
      xmlUrl: "https://xml.xml",
    });

    const command = findCommandForActionWithKey(wrapper, "xml");
    await command?.();

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith("https://xml.xml", {
      external: true,
    });
  });
});
