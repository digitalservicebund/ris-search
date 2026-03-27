import { describe, expect, it } from "vitest";
import { findNodePath, tocItemsToTreeViewItems } from "./tableOfContents";
import type { TableOfContentsItem } from "~/types/api";

describe("tableOfContents", () => {
  describe("tocItemsToTreeViewItems", () => {
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

      const result = tocItemsToTreeViewItems(items, "/heading/", "/leaf/");

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("art-1");
      expect(result[0]?.title).toBe("§ 1");
      expect(result[0]?.subtitle).toBe("Title");
      expect(result[0]?.to).toBe("/leaf/art-1");
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

      const result = tocItemsToTreeViewItems(items, "/heading/", "/leaf/");

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("chapter-1");
      expect(result[0]?.title).toBe("Chapter 1");
      expect(result[0]?.subtitle).toBe("Introduction");
      expect(result[0]?.to).toBe("/heading/chapter-1");
      expect(result[0]?.children).toHaveLength(1);
      expect(result[0]?.children?.[0]?.key).toBe("art-1");
    });

    it("encodes IDs correctly in generated URLs", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "Präöü ÄÖÜ §1",
          marker: "§ 1",
          heading: "Encoded Title",
          children: [],
        },
      ];

      const result = tocItemsToTreeViewItems(items, "/heading/", "/leaf/");

      expect(result).toHaveLength(1);
      expect(result[0]?.to).toBe("/leaf/Praeoeue AeOeUe §1");
    });
  });

  describe("findNodePath", () => {
    const treeItems = [
      {
        key: "chapter-1",
        title: "Chapter 1",
        children: [
          { key: "art-1", title: "§ 1", children: [] },
          { key: "art-2", title: "§ 2", children: [] },
        ],
      },
      {
        key: "chapter-2",
        title: "Chapter 2",
        children: [],
      },
    ];

    it("finds path to root node", () => {
      const path = findNodePath(treeItems, "chapter-2");
      expect(path).toHaveLength(1);
      expect(path?.[0]?.key).toBe("chapter-2");
    });

    it("finds path to nested node", () => {
      const path = findNodePath(treeItems, "art-1");
      expect(path).toHaveLength(2);
      expect(path?.[0]?.key).toBe("chapter-1");
      expect(path?.[1]?.key).toBe("art-1");
    });

    it("returns undefined for non-existent key", () => {
      const path = findNodePath(treeItems, "not-found");
      expect(path).toBeUndefined();
    });

    it("returns undefined for undefined key", () => {
      const path = findNodePath(treeItems, undefined);
      expect(path).toBeUndefined();
    });
  });
});
