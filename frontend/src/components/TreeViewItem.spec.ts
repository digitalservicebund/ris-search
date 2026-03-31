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
    focusedKey?: string,
    level?: number,
  ) {
    return renderSuspended(TreeViewItem, {
      props: { item, expandedKeys, selected, focusedKey, level },
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
      expect(screen.getByRole("treeitem", { name: "Parent" })).toHaveAttribute(
        "aria-expanded",
        "true",
      );
    });

    it("sets expanded state on a collapsed parent", async () => {
      await render(parent, []);
      expect(screen.getByRole("treeitem", { name: "Parent" })).toHaveAttribute(
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
      expect(emitted("update:expandedKeys")).toContainEqual([["p"]]);
    });

    it("updates the model when collapsing a node", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(parent, ["p"]);
      await user.click(screen.getByRole("button", { name: "Ebene schließen" }));
      expect(emitted("update:expandedKeys")).not.toContainEqual([["p"]]);
    });
  });

  describe("toggle all children", () => {
    it("updates the model when expanding all children", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(deepParent, []);
      await user.click(
        screen.getByRole("button", { name: "Alle Ebenen ausklappen" }),
      );
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.arrayContaining(["p", "c"]),
      ]);
    });

    it("updates the model when collapsing all children", async () => {
      const user = userEvent.setup();
      const { emitted } = await render(deepParent, ["p", "c"]);
      await user.click(
        screen.getAllByRole("button", { name: "Alle Ebenen zuklappen" })[0]!,
      );
      expect(emitted("update:expandedKeys")).toContainEqual([
        expect.not.arrayContaining(["p", "c"]),
      ]);
    });
  });

  it("emits click with the item when the title is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await render(leaf);
    await user.click(screen.getByText("Item A"));
    expect(emitted("click")).toContainEqual([leaf]);
  });

  it("emits model update with the item key when the title is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = await render(leaf);
    await user.click(screen.getByText("Item A"));
    expect(emitted("update:selected")).toContainEqual(["a"]);
  });

  it("toggles a parent when clicking the non-link header area", async () => {
    const user = userEvent.setup();
    const { container, emitted } = await render(parent, []);

    const content = container.querySelector(".content");
    expect(content).toBeTruthy();
    await user.click(content!);

    expect(emitted("update:expandedKeys")).toContainEqual([["p"]]);
    expect(emitted("click")).not.toContainEqual([parent]);
  });

  describe("link", () => {
    const linkedLeaf: TreeItem = {
      key: "a",
      title: "Item A",
      to: "/",
    };

    it("renders a link when `to` is set", async () => {
      await render(linkedLeaf);
      expect(screen.getByRole("link", { name: "Item A" })).toBeInTheDocument();
    });

    it("renders a button for the content when `to` is not set", async () => {
      await render(leaf);
      expect(screen.getByRole("button")).toBeInTheDocument();
    });
  });

  describe("subtitle", () => {
    it("renders the subtitle on a leaf item", async () => {
      await render({ key: "a", title: "Item A", subtitle: "A description" });
      expect(screen.getByText("A description")).toBeInTheDocument();
    });

    it("renders the subtitle on a parent item", async () => {
      await render({
        key: "p",
        title: "Parent",
        subtitle: "Parent description",
        children: [{ key: "c1", title: "Child 1" }],
      });
      expect(screen.getByText("Parent description")).toBeInTheDocument();
    });

    it("renders the subtitle on a child item", async () => {
      await render(
        {
          key: "p",
          title: "Parent",
          children: [
            { key: "c1", title: "Child 1", subtitle: "Child description" },
          ],
        },
        ["p"],
      );
      expect(screen.getByText("Child description")).toBeInTheDocument();
    });

    it("does not render a subtitle when absent", async () => {
      await render(leaf);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
      expect(screen.queryByText(/description/i)).not.toBeInTheDocument();
    });

    it("includes the subtitle in the accessible label of a leaf item", async () => {
      await render({ key: "a", title: "Item A", subtitle: "A description" });
      expect(
        screen.getByRole("treeitem", { name: "Item A, A description" }),
      ).toBeInTheDocument();
    });

    it("includes the subtitle in the accessible label of a parent item", async () => {
      await render(
        {
          key: "p",
          title: "Parent",
          subtitle: "Parent description",
          children: [{ key: "c1", title: "Child 1" }],
        },
        ["p"],
      );
      expect(
        screen.getByRole("treeitem", {
          name: "Parent, Parent description",
        }),
      ).toBeInTheDocument();
    });

    it("renders the subtitle when title is empty", async () => {
      await render({ key: "a", title: "", subtitle: "A description" });
      expect(screen.getByText("A description")).toBeInTheDocument();
    });

    it("uses the subtitle as the accessible label when title is empty", async () => {
      await render({ key: "a", title: "", subtitle: "A description" });
      expect(
        screen.getByRole("treeitem", { name: "A description" }),
      ).toBeInTheDocument();
    });

    it("renders when only subtitle is provided", async () => {
      await render({ key: "a", subtitle: "A description" });
      expect(screen.getByText("A description")).toBeInTheDocument();
      expect(
        screen.getByRole("treeitem", { name: "A description" }),
      ).toBeInTheDocument();
    });
  });

  describe("aria-level", () => {
    it("defaults to level 1", async () => {
      await render(leaf);
      expect(screen.getByRole("treeitem")).toHaveAttribute("aria-level", "1");
    });

    it("uses the provided level", async () => {
      await render(leaf, [], undefined, undefined, 3);
      expect(screen.getByRole("treeitem")).toHaveAttribute("aria-level", "3");
    });

    it("increments the level for children", async () => {
      await render(parent, ["p"], undefined, undefined, 1);
      const items = screen.getAllByRole("treeitem");
      expect(items[0]).toHaveAttribute("aria-level", "1");
      expect(items[1]).toHaveAttribute("aria-level", "2");
      expect(items[2]).toHaveAttribute("aria-level", "2");
    });
  });

  describe("tabindex", () => {
    it("makes item focusable when its key matches the focused key", async () => {
      await render(leaf, [], undefined, "a");
      expect(screen.getByRole("treeitem")).toHaveAttribute("tabindex", "0");
    });

    it("prevents focus when focused key doesn't match", async () => {
      await render(leaf, [], undefined, "other");
      expect(screen.getByRole("treeitem")).toHaveAttribute("tabindex", "-1");
    });

    it("prevents focus by default", async () => {
      await render(leaf);
      expect(screen.getByRole("treeitem")).toHaveAttribute("tabindex", "-1");
    });

    it("prevents focus on inner elements of a parent node", async () => {
      await render(parent, [], undefined, "p");
      const buttons = screen.getAllByRole("button");
      for (const button of buttons) {
        expect(button).toHaveAttribute("tabindex", "-1");
      }
    });

    it("prevents focus on inner elements of a leaf node", async () => {
      await render(leaf, [], undefined, "a");
      const button = screen.getByRole("button");
      expect(button).toHaveAttribute("tabindex", "-1");
    });

    it("prevents focus on inner elements of a parent node with a link", async () => {
      const linkedLeaf: TreeItem = {
        key: "a",
        title: "Item A",
        to: "/",
      };
      await render(linkedLeaf, [], undefined, "a");
      expect(screen.getByRole("link")).toHaveAttribute("tabindex", "-1");
    });
  });
});
