import type { Page } from "~/components/Pagination.vue";
import { DocumentKind } from "~/types/api";
import {
  buildLuceneQuery,
  getLuceneSearchPath,
} from "~/utils/search/luceneSearch";

/** Document kinds shown in the "Aktuelles" section. */
export const RECENT_UPDATES_DOCUMENT_KINDS = [
  DocumentKind.Norm,
  DocumentKind.CaseLaw,
  DocumentKind.AdministrativeDirective,
  DocumentKind.Literature,
] as const;

/** A document kind shown in the "Aktuelles" section. */
export type RecentUpdatesDocumentKind =
  (typeof RECENT_UPDATES_DOCUMENT_KINDS)[number];

const queries: Record<RecentUpdatesDocumentKind, string> = {
  [DocumentKind.Norm]: buildLuceneQuery(
    "DATUM:[now-10d/d TO now+10d/d]",
    { type: "allTime" },
    DocumentKind.Norm,
  ),
  [DocumentKind.CaseLaw]: buildLuceneQuery(
    "",
    { type: getDefaultDateFilterType(DocumentKind.CaseLaw) },
    DocumentKind.CaseLaw,
  ),
  [DocumentKind.Literature]: buildLuceneQuery(
    "",
    { type: getDefaultDateFilterType(DocumentKind.Literature) },
    DocumentKind.Literature,
  ),
  [DocumentKind.AdministrativeDirective]: buildLuceneQuery(
    "",
    { type: getDefaultDateFilterType(DocumentKind.AdministrativeDirective) },
    DocumentKind.AdministrativeDirective,
  ),
};

/**
 * Loads the results shown in the "Aktuelles" section of the landing page.
 *
 * @returns Search results
 */
export async function useRecentUpdates() {
  const searches = RECENT_UPDATES_DOCUMENT_KINDS.map((documentKind) => {
    const url = getLuceneSearchPath(documentKind);

    const query = queries[documentKind];

    const request = useRisBackend<Page>(url, {
      key: `recent-updates-${documentKind}`,
      query: { query, size: 5, sort: "-date", pageIndex: 0 },
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
