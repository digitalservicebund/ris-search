import type { TreeItem } from "~/components/TreeView.vue";
import type { LegislationExpressionPartSchema } from "~/types/api";

type TocTargetBuilder = (id: string) => TreeItem["to"];

export function tocItemsToTreeViewItems(
  items: LegislationExpressionPartSchema[],
  getHeadingTarget: TocTargetBuilder,
  getLeafTarget: TocTargetBuilder,
): TreeItem[] {
  return items.map((child) => {
    const childTreeItem: TreeItem = {
      // Some eIDs (e.g. "art-z§§ 1 bis 3") are URL encoded in the XML. The
      // frontend router automatically de-/encodes values that are used as
      // parameters in routes. "Normalize" the value by decoding to prevent
      // double encoding/decoding and make them comparable.
      key: decodeURIComponent(child.eId),
      title: child.name,
      subtitle: child.alternativeName,
      to: getLeafTarget(child.eId),
    };

    return !child.hasPart || child.hasPart.length === 0
      ? childTreeItem
      : {
          ...childTreeItem,
          to: getHeadingTarget(child.eId),
          children: tocItemsToTreeViewItems(
            child.hasPart,
            getHeadingTarget,
            getLeafTarget,
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
