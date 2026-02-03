import type { Page } from "~/components/Pagination.vue";
import { DocumentKind } from "~/types";
import {
  dateFilterToSimpleSearchParams,
  type StrictDateFilterValue,
} from "~/utils/search/filterType";

/** Additional configuration for search API calls */
type SimpleSearchOptions = {
  /** Number of search results per page */
  itemsPerPage: MaybeRefOrGetter<string>;

  /** Index (0-based) of the page that should be loaded */
  pageIndex: MaybeRefOrGetter<number>;

  /** Sorting order */
  sort: MaybeRefOrGetter<string>;
};

export type SimpleSearchEndpointParams = {
  searchTerm: string;
  size: string;
  pageIndex: number;
  sort: string;
  court?: string;
  dateFrom?: string;
  dateTo?: string;
  typeGroup?: string;
  mostRelevantOn?: string;
};

/**
 * Provides access to the simple search API.
 *
 * @param query - String search query
 * @param documentKindAndGroup - Type of document to search for
 * @param dateFilter - Date filter to apply to the results
 * @param court - For case law: court name to filter by
 * @returns State and context for interacting with the simple search
 */
export async function useSimpleSearch(
  query: MaybeRefOrGetter<string>,
  documentKindAndGroup: MaybeRefOrGetter<{
    documentKind: DocumentKind;
    typeGroup?: string;
  }>,
  dateFilter: MaybeRefOrGetter<StrictDateFilterValue | undefined>,
  court: MaybeRefOrGetter<string | undefined>,
  {
    itemsPerPage = "10",
    pageIndex = 0,
    sort = "default",
  }: Partial<SimpleSearchOptions>,
) {
  const searchEndpointUrl = computed(() => {
    const { documentKind } = toValue(documentKindAndGroup);
    const baseUrl = `/v1`;

    if (documentKind === DocumentKind.CaseLaw) {
      return baseUrl + "/case-law";
    } else if (documentKind === DocumentKind.Norm) {
      return baseUrl + "/legislation";
    } else if (documentKind === DocumentKind.Literature) {
      return baseUrl + "/literature";
    } else if (documentKind === DocumentKind.AdministrativeDirective) {
      return baseUrl + "/administrative-directive";
    } else {
      return baseUrl + "/document";
    }
  });

  const combinedQuery = computed<SimpleSearchEndpointParams>(() => {
    const { documentKind, typeGroup } = toValue(documentKindAndGroup);

    const result: SimpleSearchEndpointParams = {
      searchTerm: toValue(query),
      size: toValue(itemsPerPage),
      pageIndex: toValue(pageIndex),
      sort: toValue(sort),
    };

    // Date filter
    const dateFilterVal = toValue(dateFilter);
    if (dateFilterVal) {
      const filter = dateFilterToSimpleSearchParams(dateFilterVal);
      if (filter?.dateTo) result.dateTo = filter.dateTo;
      if (filter?.dateFrom) result.dateFrom = filter.dateFrom;
    }

    // Case-law specific parameters
    if (documentKind == DocumentKind.CaseLaw) {
      if (typeGroup && typeGroup !== "all") {
        result.typeGroup = typeGroup;
      }

      const courtVal = toValue(court);
      if (courtVal) result.court = courtVal;
    }

    // Norm-specific parameters
    if (
      documentKind === DocumentKind.All ||
      documentKind === DocumentKind.Norm
    ) {
      result.mostRelevantOn = getCurrentDateInGermanyFormatted();
    }

    return result;
  });

  const { data, error, status, execute } = await useRisBackend<Page>(
    searchEndpointUrl,
    {
      query: combinedQuery,
      watch: false,
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
