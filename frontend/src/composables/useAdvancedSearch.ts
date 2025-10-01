import { toValue } from "vue";
import {
  dateFilterToQuery,
  type DateFilterValue,
} from "~/components/AdvancedSearch/filterType";
import type { Page } from "~/components/Pagination/Pagination";
import { DocumentKind } from "~/types";

type UseAdvancedSearchOptions = {
  itemsPerPage: MaybeRefOrGetter<string>;
  pageIndex: MaybeRefOrGetter<number>;
  sort: MaybeRefOrGetter<string>;
};

export async function useAdvancedSearch(
  query: MaybeRefOrGetter<string>,
  documentKind: MaybeRefOrGetter<DocumentKind>,
  dateFilter: MaybeRefOrGetter<DateFilterValue>,
  {
    itemsPerPage = "50",
    pageIndex = 0,
    sort = "default",
  }: Partial<UseAdvancedSearchOptions>,
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

    return {
      query: result,
      size: toValue(itemsPerPage),
      sort: toValue(sort),
      pageIndex: toValue(pageIndex),
    };
  });

  const { data, error, status, pending, execute } = await useFetch<Page>(
    searchEndpointUrl,
    {
      query: combinedQuery,

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
    searchResults: data,
    searchError: error,
    searchStatus: status,
    searchIsPending: pending,
    submitSearch: execute,
    submitSearch: executeWhenValid,
    totalItemCount: computed(() => data.value?.totalItems ?? 0),
  };
}
