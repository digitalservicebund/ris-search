import { computed } from "vue";
import type { DocumentKind } from "~/types/api";
import { formatDocumentKind } from "~/utils/displayValues";

export type UseAdvancedSearchTitleInput = {
  documentKind: Ref<DocumentKind>;
  pageIndex: Ref<number>;
};

export function buildAdvancedSearchTitle(
  documentKind: DocumentKind,
  pageIndex: number,
): string {
  const pageSuffix = pageIndex > 0 ? `, Seite ${pageIndex + 1}` : "";

  return `${formatDocumentKind(documentKind)}, Erweiterte Suche${pageSuffix}`;
}

export function useAdvancedSearchTitle({
  documentKind,
  pageIndex,
}: UseAdvancedSearchTitleInput) {
  useHead({
    title: computed(() =>
      buildAdvancedSearchTitle(documentKind.value, pageIndex.value),
    ),
  });
}
