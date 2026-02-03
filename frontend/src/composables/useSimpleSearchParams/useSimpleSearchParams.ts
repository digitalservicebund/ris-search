import _ from "lodash";
import { computed, ref, watch } from "vue";
import type { DateSearchMode } from "./dateParams";
import { useDateParams } from "./dateParams";
import {
  addDefaults,
  defaultParams,
  getInitialState,
  omitDefaults,
} from "./getInitialState";
import type { LocationQuery, LocationQueryRaw, Router } from "#vue-router";
import { usePostHog } from "~/composables/usePostHog";
import { DocumentKind } from "~/types";
import { sortMode } from "~/utils/search/sortMode";

export { DateSearchMode } from "./dateParams";

export type DocumentKindSelectable = Omit<DocumentKind, DocumentKind.All>;

export interface QueryParams {
  query: string;
  category: string;
  itemsPerPage: number;
  pageIndex: number;
  sort: string;
  date?: string;
  dateAfter?: string; // inclusive (greater-than-equal)
  dateBefore?: string; // inclusive (greater-than-equal)
  dateSearchMode: DateSearchMode;
  court?: string;
}

/**
 * Converts query values in query to strings, as they are represented in URLs.
 */
function normalizeQuery(
  query: LocationQueryRaw,
): Record<string, string | undefined> {
  const result: Record<string, string> = {};
  for (const [key, value] of Object.entries(query)) {
    if (value) {
      result[key] = value.toString();
    }
  }
  return result;
}

/**
 * Composable for managing search parameters synchronized with URL query params.
 * Creates reactive state from the current route query and keeps URL in sync.
 */
export function useSimpleSearchParams() {
  const router = useRouter();
  const route = useRoute();
  const initialState = getInitialState(route.query);

  /*
      #############
      #   state   #
      #############
  */
  const query = ref(initialState.query);
  const pageIndex = ref(initialState.pageIndex);
  const _category = ref(initialState.category);
  const itemsPerPage = ref(initialState.itemsPerPage);
  const sort = ref(initialState.sort);
  const { date, dateAfter, dateBefore, dateSearchMode, ...dateParams } =
    useDateParams(initialState);
  const court = ref(initialState.court);

  /*
      ##############
      #  getters  #
      ##############
  */
  const params = computed(() => {
    return {
      query: query.value,
      pageIndex: pageIndex.value,
      itemsPerPage: itemsPerPage.value,
      category: _category.value,
      sort: sort.value,
      date: date.value,
      dateAfter: dateAfter.value,
      dateBefore: dateBefore.value,
      court: court.value,
    };
  });

  const category = computed({
    get: () => _category.value,
    set: (newValue) => {
      _category.value = newValue;
      if (!newValue.startsWith(DocumentKind.CaseLaw)) {
        // reset CaseLaw-specific options
        court.value = defaultParams.court;
        if (sort.value.endsWith(sortMode.courtName)) {
          sort.value = defaultParams.sort;
        }
      }
      pageIndex.value = 0; // reset page number
    },
  });

  /*
      ##############
      #  actions  #
      ##############
  */
  const setQuery = (value: string) => (query.value = value);
  const setPageNumber = (value: number) => (pageIndex.value = value);
  const setItemsPerPage = (value: number) => (itemsPerPage.value = value);
  const setSort = (value: string) => (sort.value = value);

  function reinitializeFromQuery(routerQuery: LocationQuery) {
    const initialState = getInitialState(routerQuery);

    query.value = initialState.query;
    pageIndex.value = initialState.pageIndex;
    category.value = initialState.category;
    itemsPerPage.value = initialState.itemsPerPage;
    sort.value = initialState.sort;
    court.value = initialState.court;
    dateParams.reset(initialState);
  }

  /*
  ##############
  #  watchers  #
  ##############
  */

  /*
   track what query has been set by the composable, in order to prevent infinite
   update loops
  */
  const storeQuery = ref(normalizeQuery(omitDefaults(params.value)));

  const { searchPerformed } = usePostHog();
  const updateRouterQuery = (router: Router, params: LocationQueryRaw) => {
    const query = omitDefaults(params);
    const previousQuery = storeQuery.value;
    searchPerformed("simple", addDefaults(query), addDefaults(previousQuery));
    const normalized = normalizeQuery(query);
    storeQuery.value = normalized;
    return router.push({
      ...route,
      query: normalized,
    });
  };

  watch(params, () => updateRouterQuery(router, params.value));

  watch(
    () => route.query,
    async (newQuery) => {
      // prevent infinite loops
      const needsUpdate = !_.isEqual(toRaw(storeQuery.value), newQuery);
      if (needsUpdate) {
        reinitializeFromQuery(newQuery);
      }
    },
  );

  return {
    query,
    pageIndex,
    category,
    itemsPerPage,
    sort,
    court,
    params,
    setQuery,
    setPageNumber,
    setItemsPerPage,
    setSort,
    date,
    dateAfter,
    dateBefore,
    dateSearchMode,
  };
}
