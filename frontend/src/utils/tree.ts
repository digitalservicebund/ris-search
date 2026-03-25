import type { TreeItem } from "~/components/TreeView.vue";

/**
 * Collects the item's own key and all descendant keys that are parents
 * (i.e. nodes that can be expanded). Leaf nodes are excluded.
 */
export function getExpandableKeys(item: TreeItem): string[] {
  if (!item.children?.length) return [];
  return [item.key, ...item.children.flatMap(getExpandableKeys)];
}

/** Returns true if the item and all its expandable descendants are expanded. */
export function isDeepExpanded(
  item: TreeItem,
  expandedKeys: string[],
): boolean {
  const keys = getExpandableKeys(item);
  return keys.every((k) => expandedKeys.includes(k));
}

/**
 * Toggles the deep expanded state of the item and all its descendants:
 * - if all are expanded, collapses all
 * - otherwise (none or mixed), expands all
 *
 * Returns the new expandedKeys array.
 */
export function toggleDeep(item: TreeItem, expandedKeys: string[]): string[] {
  const keys = getExpandableKeys(item);
  if (isDeepExpanded(item, expandedKeys)) {
    const toRemove = new Set(keys);
    return expandedKeys.filter((k) => !toRemove.has(k));
  } else {
    const existing = new Set(expandedKeys);
    const toAdd = keys.filter((k) => !existing.has(k));
    return [...expandedKeys, ...toAdd];
  }
}
