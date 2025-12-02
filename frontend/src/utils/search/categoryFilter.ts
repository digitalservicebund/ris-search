import type { MenuItem } from "primevue/menuitem";
import { DocumentKind } from "~/types";

export const categoryFilterItems: MenuItem[] = [
  {
    label: "Alle Dokumentarten",
    key: DocumentKind.All,
  },
  {
    label: "Gesetze & Verordnungen",
    key: DocumentKind.Norm,
  },
  {
    label: "Gerichtsentscheidungen",
    key: DocumentKind.CaseLaw,
    items: [
      {
        label: "Alle Gerichtsentscheidungen",
        key: `${DocumentKind.CaseLaw}.all`,
      },
      {
        label: "Urteil",
        key: `${DocumentKind.CaseLaw}.urteil`,
      },
      {
        label: "Beschluss",
        key: `${DocumentKind.CaseLaw}.beschluss`,
      },
      {
        label: "Sonstige Entscheidungen",
        key: `${DocumentKind.CaseLaw}.other`,
      },
    ],
  },
  {
    label: "Verwaltungsvorschriften",
    key: DocumentKind.AdministrativeDirective,
  },
  {
    label: "Literaturnachweise",
    key: DocumentKind.Literature,
  },
];

export const computeExpandedKeys = (category: string) => {
  if (!category) {
    return { [DocumentKind.All]: true };
  }
  const parts = category.split("."); // split e.g., R.urteil into R and urteil
  const result: Record<string, boolean> = {};

  findActiveItem(categoryFilterItems, "", parts, result);
  return result;
};

function findActiveItem(
  subtree: MenuItem[],
  prefix: string,
  parts: string[],
  result: Record<string, boolean>,
) {
  const key = prefix ? prefix + "." + parts[0] : parts[0];

  if (!key) {
    return;
  }

  result[key] = true;

  const activeItem = subtree.find((item) => item.key === key);
  const children = activeItem?.items;

  if (parts.length === 1 && children && children.length > 0) {
    // no specific child selected, highlight "all" subtype, which comes first
    const firstChild = children[0];
    if (firstChild?.key) {
      result[firstChild.key] = true;
    }
  }

  if (children && parts.length > 1) {
    findActiveItem(children, key, parts.slice(1), result);
  }
}
