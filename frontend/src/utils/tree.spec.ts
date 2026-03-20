import { describe, expect, it } from "vitest";
import type { TreeItem } from "~/components/TreeView.vue";
import { getExpandableKeys, isDeepExpanded, toggleDeep } from "./tree";

describe("tree", () => {
  const leaf: TreeItem = { key: "leaf", title: "Leaf" };

  const shallow: TreeItem = {
    key: "root",
    title: "Root",
    children: [
      { key: "child-1", title: "Child 1" },
      { key: "child-2", title: "Child 2" },
    ],
  };

  const deep: TreeItem = {
    key: "root",
    title: "Root",
    children: [
      {
        key: "mid",
        title: "Mid",
        children: [{ key: "leaf", title: "Leaf" }],
      },
    ],
  };

  describe("getExpandableKeys", () => {
    it("returns empty array for a leaf node", () => {
      expect(getExpandableKeys(leaf)).toEqual([]);
    });

    it("returns only the item's own key when children are all leaves", () => {
      expect(getExpandableKeys(shallow)).toEqual(["root"]);
    });

    it("returns the item key and all descendant parent keys", () => {
      expect(getExpandableKeys(deep)).toEqual(["root", "mid"]);
    });
  });

  describe("isDeepExpanded", () => {
    it("returns true for a leaf node regardless of expandedKeys", () => {
      expect(isDeepExpanded(leaf, [])).toBe(true);
    });

    it("returns true when all expandable keys are expanded", () => {
      expect(isDeepExpanded(deep, ["root", "mid"])).toBe(true);
    });

    it("returns false when none are expanded", () => {
      expect(isDeepExpanded(deep, [])).toBe(false);
    });

    it("returns false when only partially expanded", () => {
      expect(isDeepExpanded(deep, ["root"])).toBe(false);
    });
  });

  describe("toggleDeep", () => {
    it("expands all when none are expanded", () => {
      expect(toggleDeep(deep, [])).toEqual(
        expect.arrayContaining(["root", "mid"]),
      );
    });

    it("expands all when only partially expanded", () => {
      expect(toggleDeep(deep, ["root"])).toEqual(
        expect.arrayContaining(["root", "mid"]),
      );
    });

    it("collapses all when fully expanded", () => {
      expect(toggleDeep(deep, ["root", "mid"])).not.toEqual(
        expect.arrayContaining(["root", "mid"]),
      );
    });

    it("preserves unrelated keys when expanding", () => {
      expect(toggleDeep(deep, ["other"])).toContain("other");
    });

    it("preserves unrelated keys when collapsing", () => {
      expect(toggleDeep(deep, ["root", "mid", "other"])).toContain("other");
    });

    it("does nothing to expandedKeys for a leaf node", () => {
      expect(toggleDeep(leaf, ["other"])).toEqual(["other"]);
    });
  });
});
