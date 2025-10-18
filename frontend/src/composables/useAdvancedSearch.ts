import { toValue } from "vue";
import { useFetch } from "#app";
import {
  dateFilterToQuery,
  type DateFilterValue,
} from "~/components/AdvancedSearch/filterType";
import type { Page } from "~/components/Pagination/Pagination";
import { DocumentKind } from "~/types";

/** Additional configuration for search API calls */
type AdvancedSearchOptions = {
  /** Number of search results per page */
  itemsPerPage: MaybeRefOrGetter<string>;

  /** Index (0-based) of the page that should be loaded */
  pageIndex: MaybeRefOrGetter<number>;

  /** Sorting order */
  sort: MaybeRefOrGetter<string>;
};

/**
 * Provides access to the advanced search API.
 *
 * @param query Lucene search query to be submitted
 * @param documentKind Type of documents to search for
 * @param dateFilter Date filter to apply to the results
 * @returns State and context for interacting with advanced search
 */
export async function useAdvancedSearch(
  query: MaybeRefOrGetter<string>,
  documentKind: MaybeRefOrGetter<DocumentKind>,
  dateFilter: MaybeRefOrGetter<DateFilterValue>,
  {
    itemsPerPage = "50",
    pageIndex = 0,
    sort = "default",
  }: Partial<AdvancedSearchOptions>,
) {
  const searchEndpointUrl = computed(() => {
    const documentKindVal = toValue(documentKind);
    const baseUrl = "/v1/document/lucene-search";

    if (documentKindVal === DocumentKind.CaseLaw) {
      return baseUrl + "/case-law";
    } else if (documentKindVal === DocumentKind.Norm) {
      return baseUrl + "/legislation";
    } else return baseUrl;
  });

  const combinedQuery = computed(() => {
    let result = toValue(query);
    const dateQuery = dateFilterToQuery(
      toValue(dateFilter),
      toValue(documentKind),
    );

    if (dateQuery) result = `(${result}) AND (${dateQuery})`;

    return {
      query: result,
      size: toValue(itemsPerPage),
      sort: toValue(sort),
      pageIndex: toValue(pageIndex),
    };
  });

  const config = useRuntimeConfig();

  const { data, error, status, pending, execute } = await useFetch<Page>(
    searchEndpointUrl,
    {
      query: combinedQuery,

      baseURL: config.public.backendURL,

      // immediate always executes even if the query is empty. Instead the
      // component should execute manually using `executeWhenValid` to make
      // sure only useful requests are submitted.
      immediate: false,

      // default watch is too eager to reload even when manually specifying
      // watch sources, so disabling it and leaving it at the discretion of the
      // component to decide when to reload.
      watch: false,
    },
  );

  async function executeWhenValid() {
    if (toValue(query)) await execute();
  }

  return {
    searchError: error,
    searchIsPending: pending,
    searchResults: data,
    searchStatus: status,
    submitSearch: executeWhenValid,
    totalItemCount: computed(() => data.value?.totalItems ?? 0),
  };
}
