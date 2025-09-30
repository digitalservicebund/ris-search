import {
  dateFilterToQuery,
  type DateFilterValue,
} from "~/components/AdvancedSearch/filterType";
import type { Page } from "~/components/Pagination/Pagination";
import { DocumentKind } from "~/types";

export async function useAdvancedSearch(
  query: MaybeRefOrGetter<string>,
  documentKind: MaybeRefOrGetter<DocumentKind>,
  dateFilter: MaybeRefOrGetter<DateFilterValue>,
) {
  const searchEndpointUrl = computed(() => {
    const documentKindVal = toValue(documentKind);
    const baseUrl = "/api/v1/document/lucene-search";

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
    return { query: result };
  });

  const { data, error, status, pending, execute } = await useFetch<Page>(
    searchEndpointUrl,
    { query: combinedQuery, immediate: true, watch: false },
  );

  return {
    searchResults: data,
    searchError: error,
    searchStatus: status,
    searchIsPending: pending,
    submitSearch: execute,
  };
}
