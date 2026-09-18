import { toValue } from "vue";
import type { Page } from "~/components/Pagination.vue";
import type { DocumentKind, LuceneSearchParams } from "~/types/api";
import type { StrictDateFilterValue } from "~/utils/search/dateFilterType";
import { itemsPerPageDefault } from "~/utils/search/itemsPerPageOptions";
import {
  buildLuceneQuery,
  getLuceneSearchPath,
} from "~/utils/search/luceneSearch";

/** Additional configuration for search API calls */
type AdvancedSearchOptions = {
  /** Number of search results per page */
  itemsPerPage: MaybeRefOrGetter<string>;

  /** Index (0-based) of the page that should be loaded */
  pageIndex: MaybeRefOrGetter<number>;

  /** Sorting order */
  sort: MaybeRefOrGetter<string>;
};

type AdvancedSearchEndpointParams = Omit<LuceneSearchParams, "size"> & {
  size: string;
};

/**
 * Provides access to the advanced search API.
 *
 * @param query - Lucene search query to be submitted
 * @param documentKind - Type of documents to search for
 * @param dateFilter - Date filter to apply to the results
 * @returns State and context for interacting with advanced search
 */
export async function useAdvancedSearch(
  query: MaybeRefOrGetter<string>,
  documentKind: MaybeRefOrGetter<DocumentKind>,
  dateFilter: MaybeRefOrGetter<StrictDateFilterValue | undefined>,
  {
    itemsPerPage = itemsPerPageDefault,
    pageIndex = 0,
    sort = "default",
  }: Partial<AdvancedSearchOptions>,
) {
  const searchEndpointUrl = computed(() =>
    getLuceneSearchPath(toValue(documentKind)),
  );

  const combinedQuery = computed<AdvancedSearchEndpointParams>(() => ({
    query: buildLuceneQuery(
      toValue(query),
      toValue(dateFilter),
      toValue(documentKind),
    ),
    size: toValue(itemsPerPage),
    sort: toValue(sort),
    pageIndex: toValue(pageIndex),
  }));

  const { data, error, status, execute } = await useRisBackend<Page>(
    searchEndpointUrl,
    {
      query: combinedQuery,

      // default watch + immediate is too eager to reload even when manually
      // specifying watch sources, so disabling it and leaving it at the
      // discretion of the component to decide when to reload.
      watch: false,
      immediate: false,

      dedupe: "defer",
    },
  );

  return {
    searchError: error,
    searchResults: data,
    searchStatus: status,
    submitSearch: execute,
    totalItemCount: computed(() => data.value?.totalItems ?? 0),
  };
}
