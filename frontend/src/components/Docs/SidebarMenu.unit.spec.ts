import { mount } from "@vue/test-utils";
import { describe, expect, it } from "vitest";
import { data } from "./navigation.data";
import SidebarMenu from "./SidebarMenu.vue";

const MenuItemStub = {
  name: "MenuItem",
  props: {
    item: { type: Object, required: true },
    root: { type: Boolean, default: false },
  },
  emits: ["click"],
  template: `<div data-testid="menu-item" @click="$emit('click')"></div>`,
};

describe("NavigationMenu.vue", () => {
  it("renders one MenuItem per data entry and passes correct props", async () => {
    const wrapper = mount(SidebarMenu, {
      props: { model: data },
      global: {
        stubs: { MenuItem: MenuItemStub },
      },
    });

    const items = wrapper.findAll('[data-testid="menu-item"]');
    expect(items.length).toBe(data.length);

    const stubs = wrapper.findAllComponents(MenuItemStub);
    for (const [idx, stub] of stubs.entries()) {
      expect(stub.props("root")).toBe(true);
      expect(stub.props("item")).toEqual(data[idx]);
    }
  });

  it('emits "selectLeaf" for leaf items and not for parents', async () => {
    const wrapper = mount(SidebarMenu, {
      props: { model: data },
      global: { stubs: { MenuItem: MenuItemStub } },
    });
    const stubs = wrapper.findAllComponents(MenuItemStub);

    await stubs[0].trigger("click"); // Get Started
    await stubs[1].trigger("click"); // Standards
    await stubs[3].trigger("click"); // Changelog
    await stubs[4].trigger("click"); // Endpoints (items: [])
    await stubs[5].trigger("click"); // Feedback
    await stubs[2].trigger("click"); // Guides - Non-Leaf

    const emitted = wrapper.emitted("selectLeaf") ?? [];
    expect(emitted.length).toBe(5);
  });

  it("does not emit when only non-leaf items are clicked", async () => {
    const model = [
      {
        text: "With children",
        link: "/docs/parent/",
        items: [{ text: "Child", link: "/docs/child" }],
      },
    ];

    const wrapper = mount(SidebarMenu, {
      props: { model },
      global: { stubs: { MenuItem: MenuItemStub } },
    });

    await wrapper.findComponent(MenuItemStub).trigger("click");

    expect(wrapper.emitted("selectLeaf")).toBeUndefined();
  });
});
