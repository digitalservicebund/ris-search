import type { MaybeRefOrGetter } from "vue";
import type { DocumentKind } from "~/types/api";

type TrackedSearch = { query: string; documentKind: DocumentKind };

type CurrentSearch = {
  query: MaybeRefOrGetter<string>;
  documentKind: MaybeRefOrGetter<DocumentKind>;
};

/**
 * Composable for tracking searches in the simple search. Call it only when the
 * user explicitly starts a search, e.g. by submitting the search field, so that
 * revisiting the search results doesn't count as a new search.
 *
 * @param currentSearch - Search the user is currently looking at, if any.
 *   Searches with the same query and document kind are not tracked again.
 */
export function useSearchTracking(currentSearch?: CurrentSearch) {
  const { track } = useTracking();

  function isCurrentSearch({ query, documentKind }: TrackedSearch) {
    if (!currentSearch) return false;

    const currentQuery = toValue(currentSearch.query).trim();
    const currentDocumentKind = toValue(currentSearch.documentKind);
    return query === currentQuery && documentKind === currentDocumentKind;
  }

  /**
   * Tracks a search. Searches without a query are not tracked, and neither are
   * searches with the same query and document kind as the current search.
   *
   * @param search - Query and document kind of the search
   */
  function trackSearch(search: TrackedSearch) {
    const trimmedSearch = { ...search, query: search.query.trim() };
    if (!trimmedSearch.query) return;
    if (isCurrentSearch(trimmedSearch)) return;

    track("search_submitted", {
      query: trimmedSearch.query,
      document_kind: trackingDocumentKinds[trimmedSearch.documentKind],
    });
  }

  return { trackSearch };
}
