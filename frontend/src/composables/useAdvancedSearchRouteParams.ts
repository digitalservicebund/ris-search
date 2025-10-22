import type { LocationQueryValue } from "vue-router";
import { navigateTo, useRoute } from "#app";
import {
  type DateFilterValue,
  isFilterType,
} from "~/components/AdvancedSearch/filterType";
import { DocumentKind } from "~/types";
import { isDocumentKind } from "~/utils/documentKind";

function tryGetPageIndexFromQuery(
  query: LocationQueryValue | LocationQueryValue[] | undefined,
) {
  let result = 0;

  if (query) {
    const parsedNumber = Number.parseInt(query.toString());
    if (Number.isFinite(parsedNumber)) result = parsedNumber;
  }

  return result;
}

export function useAdvancedSearchRouteParams() {
  const route = useRoute();

  // General parameters -------------------------------------

  const documentKind = ref<DocumentKind>(
    typeof route.query.documentKind === "string" &&
      isDocumentKind(route.query.documentKind)
      ? route.query.documentKind
      : DocumentKind.Norm,
  );

  const query = ref(route.query.q?.toString() ?? "");

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

  const dateFilter = ref<DateFilterValue>({
    type: getInitialFilterTypeFromQuery(route.query.dateFilterType),
    from: route.query.dateFilterFrom?.toString(),
    to: route.query.dateFilterTo?.toString(),
  });

  // Sort & pagination --------------------------------------

  const sort = ref(route.query.sort?.toString() ?? "default");

  const itemsPerPage = ref(route.query.itemsPerPage?.toString() ?? "50");

  const pageIndex = ref(tryGetPageIndexFromQuery(route.query.pageIndex));

  // Saving -------------------------------------------------

  function saveFilterStateToRoute() {
    navigateTo({
      query: {
        ...route.query,
        q: query.value,
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
