import type { TreeNode } from "primevue/treenode";
import type { TableOfContentsItem } from "~/types";

export function tocItemsToTreeNodes(
  items: TableOfContentsItem[],
  headingBasePath: string,
  leafBasePath: string,
): TreeNode[] {
  return items.map((child: TableOfContentsItem) => {
    const encodedChildId = encodeForUri(child.id);
    const childTreeNode = {
      key: encodedChildId,
      label: [child.marker, child.heading].join(" "),
      route: leafBasePath + encodedChildId,
    } as TreeNode;
    return !child.children || child.children.length === 0
      ? childTreeNode
      : {
          ...childTreeNode,
          label: child.marker,
          secondaryLabel: child.heading,
          route: headingBasePath + encodedChildId,
          children: tocItemsToTreeNodes(
            child.children,
            headingBasePath,
            leafBasePath,
          ),
        };
  }) as unknown as TreeNode[];
}

export function findNodePath(
  tableOfContents: TreeNode[],
  selectedKey: string | undefined,
  path: TreeNode[] = [],
): TreeNode[] | undefined {
  for (const node of tableOfContents) {
    const newPath = [...path, { ...node, children: [] }];
    if (node.key === selectedKey) {
      return newPath;
    }
    if (node.children) {
      const foundPath = findNodePath(node.children, selectedKey, newPath);
      if (foundPath) {
        return foundPath;
      }
    }
  }
}
