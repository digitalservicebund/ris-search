import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { mount } from "@vue/test-utils";
import { vi } from "vitest";
import MenuItem from "./MenuItem.vue";
import type { MenuItemProps } from "~/components/Docs/MenuItem.vue";

const mockUseRoute = vi.fn();
const mockUseRouter = vi.fn();
vi.mock("#imports", () => ({
  useRoute: () => mockUseRoute(),
  useRouter: () => mockUseRouter(),
}));

const NuxtLinkStub = {
  name: "NuxtLink",
  props: ["to"],
  emits: ["click"],
  template: `<a :href="to" v-bind="$attrs" @click="$emit('click', $event)"><slot /></a>`,
};

const MdiChevronUpStub = {
  name: "MdiChevronUp",
  template: '<i data-testid="chev-up" />',
};
const MdiChevronRightStub = {
  name: "MdiChevronRight",
  template: '<i data-testid="chev-right" />',
};

const MenuItemChildStub = {
  name: "MenuItem",
  props: ["item", "root"],
  template: `<div data-testid="child" />`,
};

const { useRouteMock } = vi.hoisted(() => {
  return {
    useRouteMock: vi.fn(() => {
      return { path: "#", hash: "" };
    }),
  };
});

mockNuxtImport("useRoute", () => {
  return useRouteMock;
});

function mountWithRoute({
  path = "/guide",
  hash = "",
  props,
}: {
  path?: string;
  hash?: string;
  props: MenuItemProps;
}) {
  useRouteMock.mockImplementation(() => {
    return { path, hash };
  });

  return mount(MenuItem, {
    props,
    global: {
      stubs: {
        NuxtLink: NuxtLinkStub,
        MdiChevronUp: MdiChevronUpStub,
        MdiChevronRight: MdiChevronRightStub,
        MenuItem: MenuItemChildStub,
      },
    },
  });
}

describe("MenuItem.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("is active when route path matches and hash equals the item hash", async () => {
    const item = {
      text: "Getting started",
      link: "/guide#getting-started",
      items: [
        { text: "Overview", link: "/guide#overview" },
        { text: "Details", link: "/guide#details" },
      ],
    };

    const wrapper = mountWithRoute({
      path: "/guide",
      hash: "#getting-started",
      props: { item, root: true },
    });

    const link = wrapper.get("a");
    expect(link.attributes("href")).toBe("#");
    expect(link.classes()).toContain("border-l-4");
    expect(link.classes()).toContain("text-blue-800");

    expect(wrapper.find('[data-testid="chev-up"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="chev-right"]').exists()).toBe(false);

    const childrenWrap = wrapper.find("div.ml-12.border-l-2.border-gray-300");
    expect(childrenWrap.exists()).toBe(true);
    expect(wrapper.findAll('[data-testid="child"]').length).toBe(2);
  });

  it("is inactive when path matches but hash does not match parent or any child", async () => {
    const item = {
      text: "Getting started",
      link: "/guide#getting-started",
      items: [
        { text: "Overview", link: "/guide#overview" },
        { text: "Details", link: "/guide#details" },
      ],
    };

    const wrapper = mountWithRoute({
      path: "/guide/anything",
      hash: "#other-hash",
      props: { item, root: false },
    });

    const link = wrapper.get("a");
    expect(link.classes()).not.toContain("border-l-4");
    expect(wrapper.find('[data-testid="chev-right"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="chev-up"]').exists()).toBe(false);
    expect(wrapper.find("div.ml-12.border-l-2.border-gray-300").exists()).toBe(
      false,
    );
  });

  it("becomes active via child hash match (matchingHashes includes children)", async () => {
    const item = {
      text: "Parent",
      link: "/guide#overview",
      items: [
        { text: "Deep", link: "/guide#deep-dive" },
        { text: "Tips", link: "/guide#pro-tips" },
      ],
    };

    const wrapper = mountWithRoute({
      path: "/guide/advanced",
      hash: "#deep-dive",
      props: { item },
    });

    const link = wrapper.get("a");
    expect(link.classes()).toContain("border-l-4");
    expect(wrapper.find('[data-testid="chev-up"]').exists()).toBe(true);
    expect(wrapper.find("div.ml-12.border-l-2.border-gray-300").exists()).toBe(
      true,
    );
  });

  it("is active when item has no hash and path matches", async () => {
    const item = {
      text: "Guide root",
      link: "/guide",
      items: [{ text: "Child", link: "/guide#child" }],
    };

    const wrapper = mountWithRoute({
      path: "/guide/anything",
      hash: "#whatever",
      props: { item, root: true },
    });

    const link = wrapper.get("a");
    expect(link.classes()).toContain("border-l-4");
    expect(wrapper.find('[data-testid="chev-up"]').exists()).toBe(true);
    expect(wrapper.find("div.ml-12.border-l-2.border-gray-300").exists()).toBe(
      true,
    );
  });

  it("is inactive when route path does not start with item path", async () => {
    const item = {
      text: "Other section",
      link: "/docs#getting-started",
      items: [{ text: "Child", link: "/docs#child" }],
    };

    const wrapper = mountWithRoute({
      path: "/guide/intro",
      hash: "#getting-started",
      props: { item },
    });

    const link = wrapper.get("a");
    expect(link.classes()).not.toContain("border-l-4");
    expect(wrapper.find('[data-testid="chev-right"]').exists()).toBe(true);
    expect(wrapper.find("div.ml-12.border-l-2.border-gray-300").exists()).toBe(
      false,
    );
  });

  it("does not emit click while menu is disabled (current implementation)", async () => {
    const item = {
      text: "Disabled link",
      link: "/guide#hash",
      items: [],
    };

    const wrapper = mountWithRoute({
      path: "/guide",
      hash: "#hash",
      props: { item },
    });

    const link = wrapper.get("a");
    await link.trigger("click");

    expect(wrapper.emitted("click")).toBeUndefined();
    expect(link.attributes("href")).toBe("#");
  });
});
