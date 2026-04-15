import { describe, expect, it } from "vitest";
import { findNodePath, tocItemsToTreeViewItems } from "./tableOfContents";
import type { TableOfContentsItem } from "~/types/api";

const getHeadingTarget = (id: string, headingPath = "/heading") => ({
  path: headingPath,
  hash: `#${id}`,
});

const getLeafTarget = (id: string, leafPath = "/leaf") => ({
  path: `${leafPath}/${id}`,
});

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

      const result = tocItemsToTreeViewItems(
        items,
        getHeadingTarget,
        getLeafTarget,
      );

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("art-1");
      expect(result[0]?.title).toBe("§ 1");
      expect(result[0]?.subtitle).toBe("Title");
      expect(result[0]?.to).toEqual({ path: "/leaf/art-1" });
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

      const result = tocItemsToTreeViewItems(
        items,
        getHeadingTarget,
        getLeafTarget,
      );

      expect(result).toHaveLength(1);
      expect(result[0]?.key).toBe("chapter-1");
      expect(result[0]?.title).toBe("Chapter 1");
      expect(result[0]?.subtitle).toBe("Introduction");
      expect(result[0]?.to).toEqual({ path: "/heading", hash: "#chapter-1" });
      expect(result[0]?.children).toHaveLength(1);
      expect(result[0]?.children?.[0]?.key).toBe("art-1");
    });

    it("uses raw IDs in generated URLs", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "Präöü ÄÖÜ §1",
          marker: "§ 1",
          heading: "Encoded Title",
          children: [],
        },
      ];

      const result = tocItemsToTreeViewItems(
        items,
        getHeadingTarget,
        getLeafTarget,
      );

      expect(result).toHaveLength(1);
      expect(result[0]?.to).toEqual({ path: "/leaf/Präöü ÄÖÜ §1" });
    });

    it("decodes percent-encoded IDs for use as selection key", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "art-z%c2%a7%c2%a7%2018%20bis%2021",
          marker: "§§ 18 bis 21",
          heading: "Pre-encoded eId",
          children: [],
        },
      ];

      const result = tocItemsToTreeViewItems(
        items,
        getHeadingTarget,
        getLeafTarget,
      );

      // key must be decoded so it can be compared to route.params.eId (always decoded by Vue Router)
      expect(result[0]?.key).toBe("art-z§§ 18 bis 21");
      // to uses the raw id so the router can encode it correctly
      expect(result[0]?.to).toEqual({
        path: "/leaf/art-z%c2%a7%c2%a7%2018%20bis%2021",
      });
    });

    it("keeps raw IDs for selection and encodes hash links for router navigation", () => {
      const items: TableOfContentsItem[] = [
        {
          "@type": "TocEntry",
          id: "art-z§§ 18 bis 21",
          marker: "§§ 18 bis 21",
          heading: "Encoded hash target",
          children: [],
        },
      ];

      const result = tocItemsToTreeViewItems(
        items,
        (id: string) => ({ path: "/heading", hash: `#${id}` }),
        (id: string) => ({ path: "/leaf", hash: `#${id}` }),
      );

      expect(result[0]?.key).toBe("art-z§§ 18 bis 21");
      expect(result[0]?.to).toEqual({
        path: "/leaf",
        hash: "#art-z§§ 18 bis 21",
      });
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
