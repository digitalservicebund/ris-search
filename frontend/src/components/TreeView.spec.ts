import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import TreeView from "./TreeView.vue";

describe("TreeView", () => {
  const flatItems = [
    { key: "a", title: "Item A" },
    { key: "b", title: "Item B" },
    { key: "c", title: "Item C" },
  ];

  const nestedItems = [
    {
      key: "parent",
      title: "Parent",
      children: [
        { key: "child-1", title: "Child 1" },
        { key: "child-2", title: "Child 2" },
      ],
    },
    { key: "leaf", title: "Leaf" },
  ];

  const deepItems = [
    {
      key: "root",
      title: "Root",
      children: [
        {
          key: "mid",
          title: "Mid",
          children: [{ key: "leaf", title: "Leaf" }],
        },
      ],
    },
  ];

  it("renders a flat list of items", async () => {
    await renderSuspended(TreeView, { props: { items: flatItems } });

    expect(screen.getByRole("tree")).toBeInTheDocument();

    expect(
      screen.getByRole("treeitem", { name: "Item A" }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("treeitem", { name: "Item B" }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("treeitem", { name: "Item C" }),
    ).toBeInTheDocument();
  });

  it("renders nested items", async () => {
    await renderSuspended(TreeView, {
      props: { items: nestedItems, expandedKeys: ["parent"] },
    });
    const group = screen.getByRole("group");
    expect(group).toBeInTheDocument();
    expect(group).toHaveTextContent("Child 1");
    expect(group).toHaveTextContent("Child 2");
  });

  describe("heading and subheading", () => {
    it("renders the heading when provided", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, heading: "My heading" },
      });
      expect(
        screen.getByRole("heading", { name: "My heading" }),
      ).toBeInTheDocument();
    });

    it("does not render a heading element when heading prop is absent", async () => {
      await renderSuspended(TreeView, { props: { items: flatItems } });
      expect(screen.queryByRole("heading")).not.toBeInTheDocument();
    });

    it("renders the subheading", async () => {
      await renderSuspended(TreeView, {
        props: {
          items: flatItems,
          heading: "Heading",
          subheading: "A description",
        },
      });
      expect(screen.getByText("A description")).toBeInTheDocument();
    });

    it("does not render subheading when heading is absent", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, subheading: "A description" },
      });
      expect(screen.queryByText("A description")).not.toBeInTheDocument();
    });

    it("labels the component when heading is provided", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, heading: "Auto label" },
      });
      expect(
        screen.getByRole("tree", { name: "Auto label" }),
      ).toBeInTheDocument();
      expect(
        screen.getByRole("navigation", { name: "Auto label" }),
      ).toBeInTheDocument();
    });

    it("applies aria-label to the root element", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, label: "Table of contents" },
      });
      expect(
        screen.getByRole("tree", { name: "Table of contents" }),
      ).toBeInTheDocument();
      expect(
        screen.getByRole("navigation", { name: "Table of contents" }),
      ).toBeInTheDocument();
    });
  });

  describe("expandToKey", () => {
    it("expands ancestor nodes to reveal the target item", async () => {
      await renderSuspended(TreeView, {
        props: { items: deepItems, expandToKey: "leaf" },
      });

      expect(
        screen.getByRole("treeitem", { name: "Leaf" }),
      ).toBeInTheDocument();
    });

    it("does nothing when expandToKey is not set", async () => {
      await renderSuspended(TreeView, {
        props: { items: deepItems },
      });
      expect(
        screen.queryByRole("treeitem", { name: "Leaf" }),
      ).not.toBeInTheDocument();
    });

    it("does not collapse already-expanded keys when expanding ancestors", async () => {
      const items = [
        {
          key: "root",
          title: "Root",
          children: [
            {
              key: "mid",
              title: "Mid",
              children: [{ key: "leaf", title: "Leaf" }],
            },
            { key: "sibling", title: "Sibling" },
          ],
        },
      ];
      await renderSuspended(TreeView, {
        props: { items, expandedKeys: ["root"], expandToKey: "leaf" },
      });
      expect(
        screen.getByRole("treeitem", { name: "Leaf" }),
      ).toBeInTheDocument();
      expect(
        screen.getByRole("treeitem", { name: "Sibling" }),
      ).toBeInTheDocument();
    });

    it("updates when expandToKey changes", async () => {
      const items = [
        {
          key: "a",
          title: "A",
          children: [{ key: "a-child", title: "A Child" }],
        },
        {
          key: "b",
          title: "B",
          children: [{ key: "b-child", title: "B Child" }],
        },
      ];
      const view = await renderSuspended(TreeView, {
        props: { items, expandToKey: "a-child" },
      });
      expect(
        screen.getByRole("treeitem", { name: "A Child" }),
      ).toBeInTheDocument();
      expect(
        screen.queryByRole("treeitem", { name: "B Child" }),
      ).not.toBeInTheDocument();

      await view.rerender({ items, expandToKey: "b-child" });
      expect(
        screen.getByRole("treeitem", { name: "B Child" }),
      ).toBeInTheDocument();
    });
  });

  describe("toggle single item", () => {
    it("hides children when parent is collapsed", async () => {
      await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: [] },
      });
      expect(screen.queryByRole("group")).not.toBeInTheDocument();
      expect(screen.queryByText("Child 1")).not.toBeInTheDocument();
    });

    it("shows children when parent is expanded", async () => {
      await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: ["parent"] },
      });
      expect(screen.getByText("Child 1")).toBeInTheDocument();
    });
  });

  it("emits click event with the correct item when title is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await renderSuspended(TreeView, {
      props: { items: flatItems },
    });
    await user.click(screen.getByText("Item B"));
    expect(emitted("click")).toContainEqual([{ key: "b", title: "Item B" }]);
  });

  describe("selection", () => {
    it("sets selected state on all items when selected prop is not provided", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      for (const item of items) {
        expect(item).toHaveAttribute("aria-selected", "false");
      }
    });

    it("omits selected state when selection is disabled", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, selectionEnabled: false, selected: "b" },
      });

      const items = screen.getAllByRole("treeitem");
      for (const item of items) {
        expect(item).not.toHaveAttribute("aria-selected");
      }
    });
  });

  describe("tabindex manipulation", () => {
    it("sets tabindex 0 on the first item by default", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      expect(items[0]).toHaveAttribute("tabindex", "0");
      expect(items[1]).toHaveAttribute("tabindex", "-1");
      expect(items[2]).toHaveAttribute("tabindex", "-1");
    });

    it("sets tabindex 0 on the selected item when provided", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, selected: "b" },
      });
      const items = screen.getAllByRole("treeitem");
      expect(items[0]).toHaveAttribute("tabindex", "-1");
      expect(items[1]).toHaveAttribute("tabindex", "0");
      expect(items[2]).toHaveAttribute("tabindex", "-1");
    });

    it("keeps a visible item focusable when the selected item is collapsed", async () => {
      await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: [], selected: "child-1" },
      });

      const parentItem = screen.getByRole("treeitem", { name: /Parent/ });
      const leafItem = screen.getByRole("treeitem", { name: "Leaf" });

      expect(parentItem).toHaveAttribute("tabindex", "0");
      expect(leafItem).toHaveAttribute("tabindex", "-1");
    });
  });

  describe("keyboard navigation", () => {
    it("moves focus down", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{ArrowDown}");
      expect(items[1]).toHaveFocus();
    });

    it("moves focus up", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems, selected: "b" },
      });
      const items = screen.getAllByRole("treeitem");
      items[1]!.focus();
      await user.keyboard("{ArrowUp}");
      expect(items[0]).toHaveFocus();
    });

    it("does not move past the last item", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems, selected: "c" },
      });
      const items = screen.getAllByRole("treeitem");
      items[2]!.focus();
      await user.keyboard("{ArrowDown}");
      expect(items[2]).toHaveFocus();
    });

    it("does not move past the first item", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{ArrowUp}");
      expect(items[0]).toHaveFocus();
    });

    it("moves focus to the first item", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems, selected: "c" },
      });
      const items = screen.getAllByRole("treeitem");
      items[2]!.focus();
      await user.keyboard("{Home}");
      expect(items[0]).toHaveFocus();
    });

    it("moves focus to the last visible item", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{End}");
      expect(items[2]).toHaveFocus();
    });

    it("expands a closed parent", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: [] },
      });
      const parentItem = screen.getByRole("treeitem", {
        name: /Parent/,
      });
      parentItem.focus();
      await user.keyboard("{ArrowRight}");
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.arrayContaining(["parent"]),
      ]);
    });

    it("moves focus to first child when parent is expanded", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: ["parent"] },
      });
      const parentItem = screen.getByRole("treeitem", { name: /Parent/ });
      parentItem.focus();
      await user.keyboard("{ArrowRight}");
      const child1 = screen.getByRole("treeitem", { name: "Child 1" });
      expect(child1).toHaveFocus();
    });

    it("does nothing when trying to expand a leaf node", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{ArrowRight}");
      expect(items[0]).toHaveFocus();
    });

    it("collapses an open parent", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: ["parent"] },
      });
      const parentItem = screen.getByRole("treeitem", { name: /Parent/ });
      parentItem.focus();
      await user.keyboard("{ArrowLeft}");
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.not.arrayContaining(["parent"]),
      ]);
    });

    it("keeps keyboard navigation working after collapsing a selected child", async () => {
      const user = userEvent.setup();
      const view = await renderSuspended(TreeView, {
        props: {
          items: nestedItems,
          expandedKeys: ["parent"],
          selected: "child-1",
        },
      });

      await view.rerender({
        items: nestedItems,
        expandedKeys: [],
        selected: "child-1",
      });

      const parentItem = screen.getByRole("treeitem", { name: /Parent/ });
      const leafItem = screen.getByRole("treeitem", { name: "Leaf" });

      expect(parentItem).toHaveAttribute("tabindex", "0");

      parentItem.focus();
      await user.keyboard("{ArrowDown}");

      expect(leafItem).toHaveFocus();
    });

    it("moves focus to parent from a child", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: {
          items: nestedItems,
          expandedKeys: ["parent"],
          selected: "child-1",
        },
      });
      const child1 = screen.getByRole("treeitem", { name: "Child 1" });
      child1.focus();
      await user.keyboard("{ArrowLeft}");
      const parentItem = screen.getByRole("treeitem", {
        name: /Parent/,
      });
      expect(parentItem).toHaveFocus();
    });

    it("does nothing when trying to move focus for a root leaf node", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{ArrowLeft}");
      expect(items[0]).toHaveFocus();
    });

    it("selects the focused item", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{Enter}");
      expect(emitted("update:selected")).toContainEqual(["a"]);
      expect(emitted("click")).toContainEqual([{ key: "a", title: "Item A" }]);
    });

    it("does not select the focused item when selection is disabled", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: flatItems, selectionEnabled: false },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("{Enter}");
      expect(emitted("update:selected")).toBeUndefined();
      expect(emitted("click")).toContainEqual([{ key: "a", title: "Item A" }]);
    });

    it("skips collapsed children when navigating", async () => {
      const user = userEvent.setup();
      await renderSuspended(TreeView, {
        props: { items: nestedItems, expandedKeys: [] },
      });
      const parentItem = screen.getByRole("treeitem", {
        name: /Parent/,
      });
      parentItem.focus();
      await user.keyboard("{ArrowDown}");
      const leafItem = screen.getByRole("treeitem", { name: "Leaf" });
      expect(leafItem).toHaveFocus();
    });

    it("deeply expands a collapsed parent with * key", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: deepItems, expandedKeys: [] },
      });
      const rootItem = screen.getByRole("treeitem", { name: "Root" });
      rootItem.focus();
      await user.keyboard("*");
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.arrayContaining(["root", "mid"]),
      ]);
    });

    it("deeply collapses a fully expanded parent with * key", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: deepItems, expandedKeys: ["root", "mid"] },
      });
      const rootItem = screen.getByRole("treeitem", { name: "Root" });
      rootItem.focus();
      await user.keyboard("*");
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.not.arrayContaining(["root", "mid"]),
      ]);
    });

    it("expands all when * is pressed on a partially expanded parent", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: deepItems, expandedKeys: ["root"] },
      });
      const rootItem = screen.getByRole("treeitem", { name: "Root" });
      rootItem.focus();
      await user.keyboard("*");
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.arrayContaining(["root", "mid"]),
      ]);
    });

    it("does nothing when * is pressed on a leaf node", async () => {
      const user = userEvent.setup();
      const { emitted } = await renderSuspended(TreeView, {
        props: { items: flatItems },
      });
      const items = screen.getAllByRole("treeitem");
      items[0]!.focus();
      await user.keyboard("*");
      expect(emitted("update:expandedKeys")).toBeUndefined();
    });
  });
});
