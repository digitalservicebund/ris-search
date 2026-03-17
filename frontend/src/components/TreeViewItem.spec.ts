import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import TreeViewItem from "./TreeViewItem.vue";
import type { TreeItem } from "./TreeView.vue";

describe("TreeViewItem", () => {
  const leaf: TreeItem = { key: "a", title: "Item A" };
  const parent: TreeItem = {
    key: "p",
    title: "Parent",
    children: [
      { key: "c1", title: "Child 1" },
      { key: "c2", title: "Child 2" },
    ],
  };
  const deepParent: TreeItem = {
    key: "p",
    title: "Parent",
    children: [
      {
        key: "c",
        title: "Child",
        children: [{ key: "g", title: "Grandchild" }],
      },
    ],
  };

  function render(
    item: TreeItem,
    expandedKeys: string[] = [],
    selected?: string,
  ) {
    return renderSuspended(TreeViewItem, {
      props: { item, expandedKeys, selected },
    });
  }

  it("renders the item", async () => {
    await render(leaf);
    expect(
      screen.getByRole("treeitem", { name: "Item A" }),
    ).toBeInTheDocument();
  });

  describe("expansion", () => {
    it("sets expanded state on an expanded parent", async () => {
      await render(parent, ["p"]);
      expect(screen.getByRole("treeitem", { name: /parent/i })).toHaveAttribute(
        "aria-expanded",
        "true",
      );
    });

    it("sets expanded state on a collapsed parent", async () => {
      await render(parent, []);
      expect(screen.getByRole("treeitem", { name: /parent/i })).toHaveAttribute(
        "aria-expanded",
        "false",
      );
    });

    it("omits expanded state on a node that can't be expanded", async () => {
      await render(leaf);
      expect(screen.getByRole("treeitem")).not.toHaveAttribute("aria-expanded");
    });

    it("can't be toggled if it doesn't have children", async () => {
      await render(leaf);
      expect(screen.queryAllByRole("button")).toHaveLength(1);
    });

    it("can be toggled if it has children", async () => {
      await render(parent);
      expect(screen.getAllByRole("button")).toHaveLength(3);
    });

    it("renders children when expanded", async () => {
      await render(parent, ["p"]);
      const group = screen.getByRole("group");
      expect(group).toHaveTextContent("Child 1");
      expect(group).toHaveTextContent("Child 2");
    });

    it("hides children when collapsed", async () => {
      await render(parent, []);
      expect(screen.queryByRole("group")).not.toBeInTheDocument();
    });
  });

  describe("selection", () => {
    it("sets selection state on the selected item", async () => {
      await render(leaf, [], "a");
      expect(screen.getByRole("treeitem")).toHaveAttribute(
        "aria-selected",
        "true",
      );
    });

    it("sets selection state on a non-selected item", async () => {
      await render(leaf, [], "other");
      expect(screen.getByRole("treeitem")).toHaveAttribute(
        "aria-selected",
        "false",
      );
    });

    it("updates the model when expanding a node", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(parent, []);
      await user.click(screen.getByRole("button", { name: "Ebene öffnen" }));
      expect((emitted("update:expandedKeys")![0] as [string[]])[0]).toContain(
        "p",
      );
    });

    it("updated the model when collapsing a node", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(parent, ["p"]);
      await user.click(screen.getByRole("button", { name: "Ebene schließen" }));
      expect(
        (emitted("update:expandedKeys")![0] as [string[]])[0],
      ).not.toContain("p");
    });
  });

  describe("toggle all children", () => {
    it("updates the model when expanding all children", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(deepParent, []);
      await user.click(
        screen.getByRole("button", { name: "Alle Ebenen ausklappen" }),
      );
      const keys = (emitted("update:expandedKeys")![0] as [string[]])[0];
      expect(keys).toContain("p");
      expect(keys).toContain("c");
    });

    it("updates the model when collapsing all children", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(deepParent, ["p", "c"]);
      await user.click(
        screen.getAllByRole("button", { name: "Alle Ebenen zuklappen" })[0]!,
      );
      const keys = (emitted("update:expandedKeys")![0] as [string[]])[0];
      expect(keys).not.toContain("p");
      expect(keys).not.toContain("c");
    });
  });

  it("emits click with the item when the title is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await render(leaf);
    await user.click(screen.getByText("Item A"));
    expect(emitted("click")).toBeTruthy();
    expect((emitted("click")![0] as [TreeItem])[0]).toMatchObject(leaf);
  });

  it("emits model update with the item key when the title is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await render(leaf);
    await user.click(screen.getByText("Item A"));
    expect((emitted("update:selected")![0] as [string])[0]).toBe("a");
  });

  it("renders the subtitle when provided", async () => {
    const itemWithSubtitle: TreeItem = {
      key: "a",
      title: "Item A",
      subtitle: "A description",
    };
    await renderSuspended(TreeViewItem, {
      props: { item: itemWithSubtitle, expandedKeys: [] },
    });
    expect(screen.getByText("A description")).toBeInTheDocument();
  });

  it("does not render a subtitle element when subtitle is absent", async () => {
    await renderSuspended(TreeViewItem, {
      props: { item: leaf, expandedKeys: [] },
    });
    // Only the title span should be present in the default slot
    expect(screen.getByText("Item A")).toBeInTheDocument();
    expect(screen.queryByText(/description/i)).not.toBeInTheDocument();
  });
});
