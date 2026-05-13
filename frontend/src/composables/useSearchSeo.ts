import { computed } from "vue";
import { DocumentKind } from "~/types/api";

export type UseSearchSeoInput = {
  query: Ref<string | undefined>;
  documentKind: Ref<DocumentKind>;
  pageIndex: Ref<number>;
  searchType: "Suche" | "Erweiterte Suche";
  description: string;
  ogTitle: string;
};

export function useSearchSeo({
  query,
  documentKind,
  pageIndex,
  searchType,
  description,
  ogTitle,
}: UseSearchSeoInput) {
  useSeo({
    title: computed(() =>
      buildSearchTitle(
        query.value,
        documentKind.value,
        pageIndex.value,
        searchType,
      ),
    ),
    description,
    ogTitle,
  });
}

function buildSearchTitle(
  query: string | undefined,
  documentKind: DocumentKind,
  pageIndex: number,
  searchType: string,
): string {
  const pageSuffix = pageIndex > 0 ? `, Seite ${pageIndex + 1}` : "";

  if (query) return `${searchType}, ${query}${pageSuffix}`;

  if (documentKind !== DocumentKind.All) {
    return `${formatDocumentKind(documentKind)}, ${searchType}${pageSuffix}`;
  }

  return `${searchType}${pageSuffix}`;
}
