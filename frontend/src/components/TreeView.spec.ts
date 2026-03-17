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

    it("labels the tree when heading is provided", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, heading: "Auto label" },
      });
      const tree = screen.getByRole("tree");
      const heading = screen.getByRole("heading", { name: "Auto label" });
      expect(tree).toHaveAttribute("aria-labelledby", heading.id);
    });

    it("applies aria-label to the root element", async () => {
      await renderSuspended(TreeView, {
        props: { items: flatItems, label: "Table of contents" },
      });
      expect(
        screen.getByRole("tree", { name: "Table of contents" }),
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
  });
});
