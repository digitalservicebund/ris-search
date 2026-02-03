import { navigateTo, useRoute } from "#app";
import type { LocationQueryValue } from "#vue-router";
import { DocumentKind } from "~/types";
import { isDocumentKind } from "~/utils/documentKind";
import { isFilterType, type DateFilterValue } from "~/utils/search/filterType";
import { searchParamToNumber, searchParamToString } from "~/utils/searchParams";

export function useSimpleSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  // TODO: Reset document-type specific options
  const documentKind = ref<DocumentKind>(DocumentKind.All);

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
    // TODO: PostHog integration

    return navigateTo({
      query: {
        ...route.query,
        query: encodeURIComponent(query.value),
        court: court.value,
        documentKind: documentKind.value,
        typeGroup: typeGroup.value ?? "",
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
