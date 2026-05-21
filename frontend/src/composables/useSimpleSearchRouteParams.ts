import { isEmpty, isEqual } from "lodash-es";
import { navigateTo, useRoute } from "#app";
import type { LocationQueryRaw, LocationQueryValue } from "#vue-router";
import { DocumentKind } from "~/types/api";
import { isDocumentKind } from "~/utils/documentKind";
import {
  isFilterType,
  type DateFilterValue,
} from "~/utils/search/dateFilterType";
import { searchParamToNumber, searchParamToString } from "~/utils/searchParams";

export function useSimpleSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  const documentKind = ref<DocumentKind>(DocumentKind.All);

  watch(
    documentKind,
    (val, oldVal) => {
      if (val !== oldVal) {
        dateFilter.value = { type: "allTime" };
      }

      if (val !== DocumentKind.CaseLaw) {
        typeGroup.value = undefined;
        court.value = undefined;
      }
    },
    {
      // Run this watcher immediately when the documentKind changes, rather than
      // scheduling it with the other watchers, since it has side effects on
      // other watched properties that can cause race conditions.
      flush: "sync",
    },
  );

  const typeGroup = ref<string>();

  const query = ref<string>("");

  // Date filter --------------------------------------------

  function getInitialFilterTypeFromQuery(
    init: LocationQueryValue | LocationQueryValue[] | undefined,
  ): DateFilterValue["type"] {
    if (typeof init !== "string" || !isFilterType(init)) {
      return "allTime";
    }

    return init;
  }

  const dateFilter = ref<DateFilterValue>({ type: "allTime" });

  // Court filter -------------------------------------------

  const court = ref<string>();

  // Sort & pagination --------------------------------------

  const sort = ref<string>("default");

  const itemsPerPage = ref<string>("10");

  const pageIndex = ref<number>(0);

  // Saving and restoring -----------------------------------

  function loadFilterStateFromRoute(routeQuery = route.query) {
    const documentKindParam = searchParamToString(routeQuery.documentKind);
    documentKind.value =
      documentKindParam && isDocumentKind(documentKindParam)
        ? documentKindParam
        : DocumentKind.All;

    query.value = decodeURIComponent(
      searchParamToString(routeQuery.query) ?? "",
    );

    court.value = searchParamToString(routeQuery.court);

    typeGroup.value = searchParamToString(routeQuery.typeGroup);

    dateFilter.value = {
      type: getInitialFilterTypeFromQuery(routeQuery.dateFilterType),
      from: searchParamToString(routeQuery.dateFilterFrom),
      to: searchParamToString(routeQuery.dateFilterTo),
    };

    sort.value = searchParamToString(routeQuery.sort) ?? "default";

    itemsPerPage.value = searchParamToString(routeQuery.itemsPerPage) ?? "10";

    pageIndex.value = searchParamToNumber(routeQuery.pageIndex, 0);
  }

  function saveFilterStateToRoute() {
    const from = { ...route.query };

    let to: LocationQueryRaw = {
      ...from,
      query: encodeURIComponent(query.value),
      court: court.value,
      documentKind: documentKind.value,
      typeGroup: typeGroup.value ?? "",
      dateFilterType: dateFilter.value.type,
      dateFilterFrom: dateFilter.value.from ?? "",
      dateFilterTo: dateFilter.value.to ?? "",
      pageIndex: pageIndex.value.toString(),
      sort: sort.value,
      itemsPerPage: itemsPerPage.value,
    };

    // Undefined values in the "to" query will be removed by the router
    // automatically. Filtering them out here because we don't care about them
    // but they might still cause comparison mismatches.
    to = Object.fromEntries(
      Object.entries(to).filter(([, val]) => {
        return val !== undefined && val !== null;
      }),
    );

    // Hash is used for skip links on the page:
    //
    // - We'll keep it if the search params haven't change or if no search has
    //   been performed yet (-> route change is due to hash change, so any
    //   change to the hash would break navigation)
    // - Remove it if the search params have changed (-> route change is due to
    //   search, so keeping the hash would cause unintended scrolling)
    const shouldKeepHash = isEmpty(from) || isEqual(from, to);

    return navigateTo({
      hash: shouldKeepHash ? route.hash : undefined,
      query: to,
    });
  }

  watch(
    () => route.query,
    (val) => loadFilterStateFromRoute(val),
    { immediate: true },
  );

  return {
    court,
    dateFilter,
    documentKind,
    itemsPerPage,
    pageIndex,
    query,
    saveFilterStateToRoute,
    sort,
    typeGroup,
  };
}
