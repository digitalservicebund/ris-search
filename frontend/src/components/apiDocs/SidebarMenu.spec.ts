import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
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
  template: `<div data-testid="menu-item" :data-root="root" :data-item="JSON.stringify(item)" @click="$emit('click')"></div>`,
};

describe("NavigationMenu", () => {
  it("renders one MenuItem per data entry and passes correct props", async () => {
    render(SidebarMenu, {
      props: { model: data },
      global: {
        stubs: { MenuItem: MenuItemStub },
      },
    });

    const items = screen.getAllByTestId("menu-item");
    expect(items.length).toBe(data.length);

    for (const [idx, item] of items.entries()) {
      expect(item.dataset.root).toBe("true");
      expect(JSON.parse(item.dataset.item || "{}")).toEqual(data[idx]);
    }
  });

  it('emits "selectLeaf" for leaf items and not for parents', async () => {
    const user = userEvent.setup();
    const { emitted } = render(SidebarMenu, {
      props: { model: data },
      global: { stubs: { MenuItem: MenuItemStub } },
    });
    const items = screen.getAllByTestId("menu-item");

    await user.click(items[0]!); // Get Started
    await user.click(items[1]!); // Standards
    await user.click(items[3]!); // Changelog
    await user.click(items[4]!); // Endpoints (items: [])
    await user.click(items[5]!); // Feedback
    await user.click(items[2]!); // Guides - Non-Leaf

    const selectLeafEmitted = emitted("selectLeaf") ?? [];
    expect(selectLeafEmitted.length).toBe(5);
  });

  it("does not emit when only non-leaf items are clicked", async () => {
    const model = [
      {
        text: "With children",
        link: "/docs/parent/",
        items: [{ text: "Child", link: "/docs/child" }],
      },
    ];

    const user = userEvent.setup();
    const { emitted } = render(SidebarMenu, {
      props: { model },
      global: { stubs: { MenuItem: MenuItemStub } },
    });

    await user.click(screen.getByTestId("menu-item"));

    expect(emitted("selectLeaf")).toBeUndefined();
  });
});
