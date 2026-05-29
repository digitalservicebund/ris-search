import { isEmpty, isEqual } from "lodash-es";
import { navigateTo, useRoute } from "#app";
import type { LocationQueryRaw } from "#vue-router";
import { DocumentKind } from "~/types/api";
import { isDocumentKind } from "~/utils/documentKind";
import {
  isFilterType,
  type DateFilterValue,
} from "~/utils/search/dateFilterType";
import { searchParamToNumber, searchParamToString } from "~/utils/searchParams";
import { itemsPerPageDefault } from "~/utils/search/itemsPerPageOptions";

export interface SearchRouteParams {
  query?: string;
  documentKind?: DocumentKind;
  typeGroup?: string;
  court?: string;
  dateFilter?: DateFilterValue;
  sort?: string;
  itemsPerPage?: string;
  pageIndex?: number;
}

export const SIMPLE_SEARCH_DEFAULTS = {
  documentKind: DocumentKind.All,
  sort: "default",
  itemsPerPage: itemsPerPageDefault,
  pageIndex: 0,
  dateFilterType: "allTime" as const,
} as const;

export function useSimpleSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  const query = computed(() => {
    const raw = searchParamToString(route.query.query) ?? "";
    try {
      return decodeURIComponent(raw);
    } catch {
      return raw;
    }
  });

  const documentKind = computed(() => {
    const param = searchParamToString(route.query.documentKind);
    return param && isDocumentKind(param)
      ? param
      : SIMPLE_SEARCH_DEFAULTS.documentKind;
  });

  const typeGroup = computed(
    () => searchParamToString(route.query.typeGroup) || undefined,
  );

  const court = computed(
    () => searchParamToString(route.query.court) || undefined,
  );

  const dateFilter = computed<DateFilterValue>(() => {
    const typeParam = searchParamToString(route.query.dateFilterType);
    const type =
      typeof typeParam === "string" && isFilterType(typeParam)
        ? typeParam
        : SIMPLE_SEARCH_DEFAULTS.dateFilterType;
    return {
      type,
      from: searchParamToString(route.query.dateFilterFrom) || undefined,
      to: searchParamToString(route.query.dateFilterTo) || undefined,
    };
  });

  const sort = computed(
    () => searchParamToString(route.query.sort) ?? SIMPLE_SEARCH_DEFAULTS.sort,
  );

  const itemsPerPage = computed(
    () =>
      searchParamToString(route.query.itemsPerPage) ??
      SIMPLE_SEARCH_DEFAULTS.itemsPerPage,
  );

  const pageIndex = computed(() =>
    searchParamToNumber(
      route.query.pageIndex,
      SIMPLE_SEARCH_DEFAULTS.pageIndex,
    ),
  );

  // Navigation helper --------------------------------------

  function navigateToSearch(
    params: SearchRouteParams,
    options?: { replace?: boolean },
  ) {
    const merged: SearchRouteParams = {
      query: query.value,
      documentKind: documentKind.value,
      typeGroup: typeGroup.value,
      court: court.value,
      dateFilter: dateFilter.value,
      sort: sort.value,
      itemsPerPage: itemsPerPage.value,
      pageIndex: pageIndex.value,
      ...params,
    };

    // When documentKind changes, reset dependent filters
    if (
      params.documentKind !== undefined &&
      params.documentKind !== documentKind.value
    ) {
      if (!params.dateFilter)
        merged.dateFilter = { type: SIMPLE_SEARCH_DEFAULTS.dateFilterType };
      if (params.documentKind !== DocumentKind.CaseLaw) {
        if (!params.typeGroup) merged.typeGroup = undefined;
        if (!params.court) merged.court = undefined;
      }
    }

    const from = { ...route.query };

    let to: LocationQueryRaw = {
      query: encodeURIComponent(merged.query ?? ""),
      court: merged.court ?? "",
      documentKind: merged.documentKind,
      typeGroup: merged.typeGroup ?? "",
      dateFilterType: merged.dateFilter?.type,
      dateFilterFrom: merged.dateFilter?.from ?? "",
      dateFilterTo: merged.dateFilter?.to ?? "",
      pageIndex: (
        merged.pageIndex ?? SIMPLE_SEARCH_DEFAULTS.pageIndex
      ).toString(),
      sort: merged.sort,
      itemsPerPage: merged.itemsPerPage,
    };

    to = Object.fromEntries(
      Object.entries(to).filter(([, val]) => {
        return val !== undefined && val !== null;
      }),
    );

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
    court,
    dateFilter,
    documentKind,
    itemsPerPage,
    navigateToSearch,
    pageIndex,
    query,
    sort,
    typeGroup,
  };
}
