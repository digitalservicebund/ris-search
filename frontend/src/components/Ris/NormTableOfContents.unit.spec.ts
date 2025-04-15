// @ts-nocheck
import { mount, RouterLinkStub } from "@vue/test-utils";
import { describe, it, expect, beforeEach } from "vitest";
import NormTableOfContents from "./NormTableOfContents.vue";
import { tocItemsToTreeNodes } from "@/utils/tableOfContents";
import type { TreeNode } from "primevue/treenode";
import type { TableOfContentsItem } from "@/types";

describe("TableOfContents Component", () => {
  let wrapper: ReturnType;

  const mockTocItems: TableOfContentsItem[] = [
    {
      "@type": "TocEntry",
      id: "chapter1",
      marker: "1",
      heading: "Chapter 1",
      children: [
        {
          "@type": "TocEntry",
          id: "section1-1",
          marker: "1.1",
          heading: "Section 1.1",
          children: [],
        },
        {
          "@type": "TocEntry",
          id: "section1-2",
          marker: "1.2",
          heading: "Section 1.2",
          children: [
            {
              "@type": "TocEntry",
              id: "subsection1-2-1",
              marker: "1.2.1",
              heading: "Subsection 1.2.1",
              children: [],
            },
          ],
        },
      ],
    },
    {
      "@type": "TocEntry",
      id: "chapter2",
      marker: "2",
      heading: "Chapter 2",
      children: [
        {
          "@type": "TocEntry",
          id: "section2-1",
          marker: "2.1",
          heading: "Section 2.1",
          children: [],
        },
      ],
    },
  ];

  const headingBasePath = "/headings/";
  const leafBasePath = "/leaf/";

  beforeEach(() => {
    const treeNodes = tocItemsToTreeNodes(
      mockTocItems,
      headingBasePath,
      leafBasePath,
    );
    wrapper = mount(NormTableOfContents, {
      props: {
        tableOfContents: treeNodes,
      },
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          IcBaselineUnfoldMore: {
            template: "<div />",
            name: "IcBaselineUnfoldMore",
          },
          IcBaselineUnfoldLess: {
            template: "<div />",
            name: "IcBaselineUnfoldLess",
          },
          IcBaselineClose: {
            template: "<div />",
            name: "IcBaselineClose",
          },
          IcBaselineFormatListBulleted: {
            template: "<div />",
            name: "IcBaselineFormatListBulleted",
          },
        },
      },
    });
  });

  describe("Initial Rendering", () => {
    it("renders correctly with given tableOfContents", () => {
      const rootLabels = wrapper.findAll(".p-tree-node-label a");
      expect(rootLabels.length).toBe(2);
      expect(rootLabels[0].text()).toBe("1"); // marker for Chapter 1
      expect(rootLabels[1].text()).toBe("2"); // marker for Chapter 2
      const rootSecondaryLabels = wrapper.findAll(".ris-label2-regular");
      expect(rootSecondaryLabels[0].text()).toBe("Chapter 1");
      expect(rootSecondaryLabels[1].text()).toBe("Chapter 2");
    });

    it("has no nodes expanded initially by default", () => {
      const expandedNodes = Object.values(wrapper.vm.expandedKeys);
      expect(expandedNodes).toStrictEqual([]);
    });

    it("does not select any node if `selectedKey` is not provided", () => {
      const selectedKeys = wrapper.vm.selectionKeys;
      expect(Object.keys(selectedKeys).length).toBe(0);
    });
  });

  describe("When `selectedKey` is provided", () => {
    it("expands nodes up to the selected key", async () => {
      const treeNodes = tocItemsToTreeNodes(
        mockTocItems,
        headingBasePath,
        leafBasePath,
      );
      wrapper = mount(NormTableOfContents, {
        props: {
          tableOfContents: treeNodes,
          selectedKey: "subsection1-2-1",
        },
        global: {
          stubs: {
            RouterLink: RouterLinkStub,
          },
        },
      });
      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(true);
      expect(wrapper.vm.expandedKeys["section1-2"]).toBe(true);
      // The leaf node 'subsection1-2-1' itself isn't stored in expandedKeys because it's a leaf but it should be selected
      expect(wrapper.vm.selectionKeys["subsection1-2-1"]).toBe(true);
    });
  });

  describe("toggleNode()", () => {
    it("expands a collapsed node and collapse an expanded node", async () => {
      // Let's toggle the first root node (Chapter 1)
      const chapter1Node = wrapper.findAll(".p-tree-node-label a")[0];
      await chapter1Node.trigger("click");

      // Since Chapter 1 is an internal node, toggling it should expand it
      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(true);

      // Then collapse
      await chapter1Node.trigger("click");
      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(false);
    });
  });

  describe("expandNode()", () => {
    it("expands a node and all its children", async () => {
      const treeNodes = wrapper.vm.props.tableOfContents as TreeNode[];
      const chapter1Node = treeNodes[0];
      // Expand the first root node and all children
      await wrapper.vm.expandNode(chapter1Node);

      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(true);
      // chapter1's children: section1-1 and section1-2
      expect(wrapper.vm.expandedKeys["section1-2"]).toBe(true);

      // The leaf node subsection1-2-1 will also be expanded
      // (though for a leaf node this doesn't have the same meaning as for internal nodes)
      expect(wrapper.vm.expandedKeys["subsection1-2-1"]).toBe(true);
    });
  });

  describe("expandAll()", () => {
    it("expands all nodes in the tree", async () => {
      await wrapper.vm.expandAll();
      expect(wrapper.vm.expandedKeys).toEqual({
        chapter1: true,
        chapter2: true,
        "section1-1": true,
        "section1-2": true,
        "section2-1": true,
        "subsection1-2-1": true,
      });
    });
  });

  describe("collapseAll()", () => {
    it("collapses all nodes in the tree", async () => {
      // First, expand all
      await wrapper.vm.expandAll();
      // Now collapse all
      await wrapper.vm.collapseAll();
      expect(Object.keys(wrapper.vm.expandedKeys).length).toBe(0);
    });
  });

  describe("toggleExpandCollapse()", () => {
    it("expands all nodes if tree is currently collapsed", async () => {
      // Initially everything is collapsed
      await wrapper.vm.toggleExpandCollapse();
      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(true);
      expect(wrapper.vm.expandedKeys["section1-2"]).toBe(true);
      expect(wrapper.vm.expandedKeys["subsection1-2-1"]).toBe(true);
      expect(wrapper.vm.expandedKeys["chapter2"]).toBe(true);
    });

    it("collapses all nodes if tree is currently expanded", async () => {
      // Expand everything first
      await wrapper.vm.toggleExpandCollapse();
      // Then toggle
      await wrapper.vm.toggleExpandCollapse();
      expect(Object.keys(wrapper.vm.expandedKeys).length).toBe(0);
    });

    it("updates the isExpanded value appropriately", async () => {
      expect(wrapper.vm.isExpanded).toBe(false);
      await wrapper.vm.toggleExpandCollapse();
      expect(wrapper.vm.isExpanded).toBe(true);
      await wrapper.vm.toggleExpandCollapse();
      expect(wrapper.vm.isExpanded).toBe(false);
    });
  });

  describe("Interactions with UI", () => {
    it("calls toggleExpandCollapse when the button is clicked", async () => {
      const button = wrapper.find("#toc-expand-collapse-button");
      await button.trigger("click");
      expect(wrapper.vm.isExpanded).toBe(true);
      await button.trigger("click");
      expect(wrapper.vm.isExpanded).toBe(false);
    });

    it("expands/collapses nodes visually when toggling", async () => {
      const chapter1Toggler = wrapper.findAll(".p-tree-node-label a")[0];
      await chapter1Toggler.trigger("click");
      expect(wrapper.vm.expandedKeys["chapter1"]).toBe(true);

      // The node "Chapter 1" expands, revealing its children: "Section 1.1" and "Section 1.2"
      const visibleNodes = wrapper.findAll(".p-tree-node-label a");
      // Now, we have root nodes and the children of the expanded node visible
      expect(visibleNodes.length).toBe(4);
    });
  });

  describe("Mobile TOC Button", () => {
    it("shows the mobile TOC button by default and hides the table of contents", () => {
      const mobileTocButton = wrapper.find("[data-testid='mobile-toc-button']");
      const toc = wrapper.find("[data-testid='table-of-contents']");
      expect(mobileTocButton.exists()).toBe(true);
      expect(toc.classes()).toContain("max-lg:data-[selected=false]:hidden");
    });

    it("shows the table of contents and hides the mobile TOC button when the mobile TOC button is clicked", async () => {
      const mobileTocButton = wrapper.find("[data-testid='mobile-toc-button']");
      expect(wrapper.vm.isTocVisible).toBe(false);
      await mobileTocButton.trigger("click");
      expect(wrapper.vm.isTocVisible).toBe(true);
      const toc = wrapper.find("[data-testid='table-of-contents']");
      expect(toc.isVisible()).toBe(true);
    });

    it("hides the table of contents and shows the mobile TOC button when the close button is clicked", async () => {
      const mobileTocButton = wrapper.find("[data-testid='mobile-toc-button']");
      expect(mobileTocButton.isVisible()).toBe(true);
      await mobileTocButton.trigger("click");
      const closeButton = wrapper.find("#toc-close-button");
      expect(wrapper.vm.isTocVisible).toBe(true);
      await closeButton.trigger("click");
      expect(wrapper.vm.isTocVisible).toBe(false);
    });

    it("hides the mobile TOC button when an item is clicked", async () => {
      const mobileTocButton = wrapper.find("[data-testid='mobile-toc-button']");
      expect(mobileTocButton.isVisible()).toBe(true);
      await mobileTocButton.trigger("click");

      const itemLink = wrapper.find(".p-tree-node-label a");
      await itemLink.trigger("click");
      expect(wrapper.vm.isTocVisible).toBe(false);
    });
  });

  describe("Expand/Collapse Button", () => {
    it("does not show the expand/collapse button if the tableOfContents is single level", async () => {
      const singleLevelTocItems: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "chapter1",
          marker: "1",
          heading: "Chapter 1",
          children: [],
        },
      ];
      const treeNodes = tocItemsToTreeNodes(
        singleLevelTocItems,
        headingBasePath,
        leafBasePath,
      );
      wrapper = mount(NormTableOfContents, {
        props: {
          tableOfContents: treeNodes,
        },
        global: {
          stubs: {
            RouterLink: RouterLinkStub,
          },
        },
      });
      const expandCollapseButton = wrapper.find("#toc-expand-collapse-button");
      expect(expandCollapseButton.exists()).toBe(false);
    });

    it("shows the expand/collapse button if the tableOfContents is multi-level", async () => {
      const expandCollapseButton = wrapper.find("#toc-expand-collapse-button");
      expect(expandCollapseButton.exists()).toBe(true);
    });

    it("shows the correct icon when the tableOfContents is collapsed", async () => {
      const expandCollapseButton = wrapper.find("#toc-expand-collapse-button");
      expect(
        expandCollapseButton
          .findComponent({ name: "IcBaselineUnfoldMore" })
          .exists(),
      ).toBe(true);
      expect(
        expandCollapseButton
          .findComponent({ name: "IcBaselineUnfoldLess" })
          .exists(),
      ).toBe(false);
    });

    it("shows the correct icon when the tableOfContents is expanded", async () => {
      const expandCollapseButton = wrapper.find("#toc-expand-collapse-button");
      await expandCollapseButton.trigger("click");
      expect(
        expandCollapseButton
          .findComponent({ name: "IcBaselineUnfoldMore" })
          .exists(),
      ).toBe(false);
      expect(
        expandCollapseButton
          .findComponent({ name: "IcBaselineUnfoldLess" })
          .exists(),
      ).toBe(true);
    });
  });

  describe("Utility Methods", () => {
    it("toggles table of contents visibility with toggleTableOfContents", () => {
      expect(wrapper.vm.isTocVisible).toBe(false);
      wrapper.vm.toggleTableOfContents();
      expect(wrapper.vm.isTocVisible).toBe(true);
      wrapper.vm.toggleTableOfContents();
      expect(wrapper.vm.isTocVisible).toBe(false);
    });

    it("sets isTocVisible to false when hideTableOfContents is called", () => {
      wrapper.vm.isTocVisible = true;
      wrapper.vm.hideTableOfContents();
      expect(wrapper.vm.isTocVisible).toBe(false);
    });
  });

  describe("Watcher on nodes", () => {
    it("updates selectionKeys and expandedKeys when nodes computed property changes", async () => {
      await wrapper.setProps({ selectedKey: "chapter2" });
      await nextTick();
      expect(wrapper.vm.expandedKeys["chapter2"]).toBe(true);
      expect(wrapper.vm.selectionKeys["chapter2"]).toBe(true);
    });
  });

  describe("Slot Rendering", () => {
    it("renders header slot content", () => {
      const headerContent = "Custom Header Content";
      const wrapperWithHeader = mount(NormTableOfContents, {
        props: {
          tableOfContents: wrapper.vm.props.tableOfContents,
        },
        slots: {
          header: `<div data-testid="header-slot">${headerContent}</div>`,
        },
        global: {
          stubs: {
            RouterLink: RouterLinkStub,
          },
        },
      });
      const headerSlot = wrapperWithHeader.find("[data-testid='header-slot']");
      expect(headerSlot.exists()).toBe(true);
      expect(headerSlot.text()).toBe(headerContent);
    });
  });

  describe("Responsive Styles and Outer Container", () => {
    it("applies responsiveStyles to the table of contents container", () => {
      const tocContainer = wrapper.find("[data-testid='table-of-contents']");
      expect(tocContainer.classes()).toContain("max-lg:fixed");
      expect(tocContainer.classes()).toContain("max-lg:left-0");
    });
  });
});
