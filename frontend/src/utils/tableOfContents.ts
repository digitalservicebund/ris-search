import type { TreeItem } from "~/components/TreeView.vue";
import type { TableOfContentsItem } from "~/types/api";

export function tocItemsToTreeViewItems(
  items: TableOfContentsItem[],
  headingBasePath: string,
  leafBasePath: string,
): TreeItem[] {
  return items.map((child) => {
    const encodedChildId = encodeForUri(child.id);
    const childTreeItem: TreeItem = {
      key: encodedChildId,
      title: child.marker,
      subtitle: child.heading,
      to: leafBasePath + encodedChildId,
    };

    return !child.children || child.children.length === 0
      ? childTreeItem
      : {
          ...childTreeItem,
          to: headingBasePath + encodedChildId,
          children: tocItemsToTreeViewItems(
            child.children,
            headingBasePath,
            leafBasePath,
          ),
        };
  });
}

type TreePathNode = {
  key?: string;
  children?: TreePathNode[];
};

export function findNodePath<T extends TreePathNode>(
  tableOfContents: T[],
  selectedKey: string | undefined,
  path: T[] = [],
): T[] | undefined {
  for (const node of tableOfContents) {
    const newPath = [...path, node];
    if (node.key === selectedKey) {
      return newPath;
    }
    if (node.children) {
      const foundPath = findNodePath(
        node.children as T[],
        selectedKey,
        newPath,
      );
      if (foundPath) {
        return foundPath;
      }
    }
  }
}
