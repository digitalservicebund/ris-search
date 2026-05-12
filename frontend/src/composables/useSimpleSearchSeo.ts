import { computed } from "vue";
import { DocumentKind } from "~/types/api";
import { formatDocumentKind } from "~/utils/displayValues";

export type UseSearchSeoInput = {
  query: Ref<string | undefined>;
  documentKind: Ref<DocumentKind>;
  pageIndex: Ref<number>;
};

export function buildSearchTitle(
  query: string | undefined,
  documentKind: DocumentKind,
  pageIndex: number,
): string {
  const pageSuffix = pageIndex > 0 ? `, Seite ${pageIndex + 1}` : "";

  if (query) return `Suche, ${query}${pageSuffix}`;

  if (documentKind !== DocumentKind.All) {
    return `${formatDocumentKind(documentKind)}, Suche${pageSuffix}`;
  }

  return `Suche${pageSuffix}`;
}

export function useSimpleSearchSeo({
  query,
  documentKind,
  pageIndex,
}: UseSearchSeoInput) {
  useSeo({
    title: computed(() =>
      buildSearchTitle(query.value, documentKind.value, pageIndex.value),
    ),
    description:
      "Finden Sie gezielt Gesetze, Verordnungen und Entscheidungen – schnell, präzise und übersichtlich.",
    ogTitle: "Suche im Rechtsinformationsportal des Bundes",
  });
}
