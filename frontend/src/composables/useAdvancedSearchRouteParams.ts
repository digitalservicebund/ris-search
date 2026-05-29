import { isEmpty, isEqual } from "lodash-es";
import { navigateTo, useRoute } from "#app";
import type { LocationQueryRaw } from "#vue-router";
import { DocumentKind } from "~/types/api";
import { isDocumentKind } from "~/utils/documentKind";
import {
  type DateFilterValue,
  isFilterType,
} from "~/utils/search/dateFilterType";
import { searchParamToNumber, searchParamToString } from "~/utils/searchParams";
import { itemsPerPageDefault } from "~/utils/search/itemsPerPageOptions";

export interface AdvancedSearchRouteParams {
  query?: string;
  documentKind?: DocumentKind;
  dateFilter?: DateFilterValue;
  sort?: string;
  itemsPerPage?: string;
  pageIndex?: number;
}

export const ADVANCED_SEARCH_DEFAULTS = {
  documentKind: DocumentKind.Norm,
  sort: "default",
  itemsPerPage: itemsPerPageDefault,
  pageIndex: 0,
} as const;

export function getDefaultDateFilterType(
  documentKind: DocumentKind,
): DateFilterValue["type"] {
  return documentKind === DocumentKind.Norm ? "currentlyInForce" : "allTime";
}

export function useAdvancedSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  const documentKind = computed(() => {
    const param = searchParamToString(route.query.documentKind);
    return param && isDocumentKind(param)
      ? param
      : ADVANCED_SEARCH_DEFAULTS.documentKind;
  });

  const query = computed(() => {
    const raw = searchParamToString(route.query.q) ?? "";
    try {
      return decodeURIComponent(raw);
    } catch {
      return raw;
    }
  });

  const dateFilter = computed<DateFilterValue>(() => {
    const typeParam = searchParamToString(route.query.dateFilterType);
    let type: DateFilterValue["type"];

    if (typeof typeParam === "string" && isFilterType(typeParam)) {
      // If the current filter is not valid for the current document kind
      if (
        typeParam === "currentlyInForce" &&
        documentKind.value !== DocumentKind.Norm
      ) {
        type = "allTime";
      } else {
        type = typeParam;
      }
    } else {
      type = getDefaultDateFilterType(documentKind.value);
    }

    return {
      type,
      from: searchParamToString(route.query.dateFilterFrom) || undefined,
      to: searchParamToString(route.query.dateFilterTo) || undefined,
    };
  });

  const sort = computed(
    () =>
      searchParamToString(route.query.sort) ?? ADVANCED_SEARCH_DEFAULTS.sort,
  );

  const itemsPerPage = computed(
    () =>
      searchParamToString(route.query.itemsPerPage) ??
      ADVANCED_SEARCH_DEFAULTS.itemsPerPage,
  );

  const pageIndex = computed(() =>
    searchParamToNumber(
      route.query.pageIndex,
      ADVANCED_SEARCH_DEFAULTS.pageIndex,
    ),
  );

  // Navigation helper --------------------------------------

  function navigateToSearch(
    params: AdvancedSearchRouteParams,
    options?: { replace?: boolean },
  ) {
    const merged: AdvancedSearchRouteParams = {
      query: query.value,
      documentKind: documentKind.value,
      dateFilter: dateFilter.value,
      sort: sort.value,
      itemsPerPage: itemsPerPage.value,
      pageIndex: pageIndex.value,
      ...params,
    };

    // When documentKind changes, reset query and date filter
    if (
      params.documentKind !== undefined &&
      params.documentKind !== documentKind.value
    ) {
      if (params.query === undefined) merged.query = "";
      if (!params.dateFilter) {
        merged.dateFilter = {
          type: getDefaultDateFilterType(params.documentKind),
        };
      }
    }

    const from = { ...route.query };

    const to: LocationQueryRaw = {
      q: encodeURIComponent(merged.query ?? ""),
      documentKind: merged.documentKind,
      dateFilterType: merged.dateFilter?.type,
      dateFilterFrom: merged.dateFilter?.from ?? "",
      dateFilterTo: merged.dateFilter?.to ?? "",
      pageIndex: (
        merged.pageIndex ?? ADVANCED_SEARCH_DEFAULTS.pageIndex
      ).toString(),
      sort: merged.sort,
      itemsPerPage: merged.itemsPerPage,
    };

    // Hash is used for skip links on the page:
    //
    // - We'll keep it if the search params haven't changed or if no search has
    //   been performed yet (-> route change is due to hash change, so any
    //   change to the hash would break navigation)
    // - Remove it if the search params have changed (-> route change is due to
    //   search, so keeping the hash would cause unintended scrolling)
    const shouldKeepHash = isEmpty(from) || isEqual(from, to);

    return navigateTo(
      {
        hash: shouldKeepHash ? route.hash : undefined,
        query: to,
      },
      { replace: options?.replace },
    );
  }

  return {
    dateFilter,
    documentKind,
    itemsPerPage,
    navigateToSearch,
    pageIndex,
    query,
    sort,
  };
}
