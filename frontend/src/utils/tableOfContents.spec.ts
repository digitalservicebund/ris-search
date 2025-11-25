import type { TreeNode } from "primevue/treenode";
import { describe, it, expect } from "vitest";
import { tocItemsToTreeNodes, findNodePath } from "./tableOfContents";
import type { TableOfContentsItem } from "~/types";

describe("tableOfContents", () => {
  describe("tocItemsToTreeNodes", () => {
    it("converts leaf items correctly", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "art-1",
          marker: "§ 1",
          heading: "Title",
          children: [],
        },
      ];

      const result = tocItemsToTreeNodes(items, "/heading/", "/leaf/");

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("art-1");
      expect(result[0]?.label).toBe("§ 1 Title");
      expect(result[0]?.route).toBe("/leaf/art-1");
      expect(result[0]?.children).toBeUndefined();
    });

    it("converts items with children correctly", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "chapter-1",
          marker: "Chapter 1",
          heading: "Introduction",
          children: [
            {
              "@type": "TocEntry",
              id: "art-1",
              marker: "§ 1",
              heading: "Scope",
              children: [],
            },
          ],
        },
      ];

      const result = tocItemsToTreeNodes(items, "/heading/", "/leaf/");

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("chapter-1");
      expect(result[0]?.label).toBe("Chapter 1");
      expect(result[0]?.secondaryLabel).toBe("Introduction");
      expect(result[0]?.route).toBe("/heading/chapter-1");
      expect(result[0]?.children).toHaveLength(1);
      expect(result[0]?.children?.[0]?.key).toBe("art-1");
    });
  });

  describe("findNodePath", () => {
    const treeNodes: TreeNode[] = [
      {
        key: "chapter-1",
        label: "Chapter 1",
        children: [
          { key: "art-1", label: "§ 1", children: [] },
          { key: "art-2", label: "§ 2", children: [] },
        ],
      },
      {
        key: "chapter-2",
        label: "Chapter 2",
        children: [],
      },
    ];

    it("finds path to root node", () => {
      const path = findNodePath(treeNodes, "chapter-2");
      expect(path).toHaveLength(1);
      expect(path?.[0]?.key).toBe("chapter-2");
    });

    it("finds path to nested node", () => {
      const path = findNodePath(treeNodes, "art-1");
      expect(path).toHaveLength(2);
      expect(path?.[0]?.key).toBe("chapter-1");
      expect(path?.[1]?.key).toBe("art-1");
    });

    it("returns undefined for non-existent key", () => {
      const path = findNodePath(treeNodes, "not-found");
      expect(path).toBeUndefined();
    });

    it("returns undefined for undefined key", () => {
      const path = findNodePath(treeNodes, undefined);
      expect(path).toBeUndefined();
    });
  });
});
