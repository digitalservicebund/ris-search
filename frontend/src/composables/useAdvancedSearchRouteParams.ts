import type { LocationQueryValue } from "vue-router";
import { navigateTo, useRoute } from "#app";
import { DocumentKind } from "~/types";
import { isDocumentKind } from "~/utils/documentKind";
import { type DateFilterValue, isFilterType } from "~/utils/search/filterType";
import { searchParamToNumber, searchParamToString } from "~/utils/searchParams";

export function useAdvancedSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  const documentKind = ref<DocumentKind>(DocumentKind.Norm);

  const query = ref<string>("");

  // Date filter --------------------------------------------

  function getInitialFilterTypeFromQuery(
    init: LocationQueryValue | LocationQueryValue[] | undefined,
  ): DateFilterValue["type"] {
    if (typeof init !== "string" || !isFilterType(init)) {
      // If the initial value is no valid filter, return the default filter
      // based on the selected document kind
      return documentKind.value === DocumentKind.Norm
        ? "currentlyInForce"
        : "allTime";
    } else if (
      init === "currentlyInForce" &&
      documentKind.value !== DocumentKind.Norm
    ) {
      // If the current filter is not valid for the current selection, return
      // a different filter
      return "allTime";
    }
    // Return parsed filter
    else return init;
  }

  const dateFilter = ref<DateFilterValue>({ type: "allTime" });

  // Sort & pagination --------------------------------------

  const sort = ref<string>("default");

  const itemsPerPage = ref<string>("50");

  const pageIndex = ref<number>(0);

  // Saving and restoring -----------------------------------

  function loadFilterStateFromRoute(routeQuery = route.query) {
    const documentKindParam = searchParamToString(routeQuery.documentKind);
    documentKind.value =
      documentKindParam && isDocumentKind(documentKindParam)
        ? documentKindParam
        : DocumentKind.Norm;

    query.value = decodeURIComponent(searchParamToString(routeQuery.q) ?? "");

    dateFilter.value = {
      type: getInitialFilterTypeFromQuery(routeQuery.dateFilterType),
      from: searchParamToString(routeQuery.dateFilterFrom),
      to: searchParamToString(routeQuery.dateFilterTo),
    };

    sort.value = searchParamToString(routeQuery.sort) ?? "default";

    itemsPerPage.value = searchParamToString(routeQuery.itemsPerPage) ?? "50";

    pageIndex.value = searchParamToNumber(routeQuery.pageIndex, 0);
  }

  function saveFilterStateToRoute() {
    return navigateTo({
      query: {
        ...route.query,
        q: encodeURIComponent(query.value),
        documentKind: documentKind.value,
        dateFilterType: dateFilter.value.type,
        dateFilterFrom: dateFilter.value.from ?? "",
        dateFilterTo: dateFilter.value.to ?? "",
        pageIndex: pageIndex.value,
        sort: sort.value,
        itemsPerPage: itemsPerPage.value,
      },
    });
  }

  watch(
    () => route.query,
    (val) => loadFilterStateFromRoute(val),
    { immediate: true },
  );

  return {
    dateFilter,
    documentKind,
    itemsPerPage,
    pageIndex,
    query,
    saveFilterStateToRoute,
    sort,
  };
}
