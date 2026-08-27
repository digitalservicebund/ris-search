import type { Page } from "~/components/Pagination.vue";
import { DocumentKind } from "~/types/api";
import {
  buildLuceneQuery,
  getLuceneSearchPath,
} from "~/utils/search/luceneSearch";

/** Document kinds shown in the "Aktuelles" section. */
export const RECENT_SECTION_DOCUMENT_KINDS = [
  DocumentKind.Norm,
  DocumentKind.CaseLaw,
  DocumentKind.AdministrativeDirective,
  DocumentKind.Literature,
] as const;

/** A document kind shown in the "Aktuelles" section. */
export type RecentSectionDocumentKind =
  (typeof RECENT_SECTION_DOCUMENT_KINDS)[number];

/** Number of results shown per document kind. */
const RESULTS_PER_KIND = 5;

/**
 * Loads the results shown in the "Aktuelles" section of the landing page.
 *
 * @returns Search results
 */
export async function useRecentSectionSearchResults() {
  const searches = RECENT_SECTION_DOCUMENT_KINDS.map((documentKind) => {
    const url = getLuceneSearchPath(documentKind);

    const query = buildLuceneQuery(
      "",
      { type: getDefaultDateFilterType(documentKind) },
      documentKind,
    );

    const request = useRisBackend<Page>(url, {
      key: `recent-section-${documentKind}`,
      query: {
        query,
        size: RESULTS_PER_KIND,
        sort: ADVANCED_SEARCH_DEFAULTS.sort,
        pageIndex: ADVANCED_SEARCH_DEFAULTS.pageIndex,
      },
    });

    return {
      documentKind,
      searchResults: computed(() => request.data.value?.member ?? []),
      searchError: request.error,
      request,
    };
  });

  await Promise.all(searches.map(({ request }) => request));

  return searches.map(({ request: _request, ...rest }) => rest);
}
