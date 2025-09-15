import _ from "lodash";
import { defineStore } from "pinia";
import { computed, ref, watch } from "vue";
import type { LocationQueryRaw, Router } from "vue-router";
import {
  addDefaults,
  defaultParams,
  getInitialState,
  omitDefaults,
} from "./getInitialState";
import type { LocationQuery } from "#vue-router";
import { sortMode } from "~/components/types";
import type { DateSearchMode } from "~/stores/searchParams/dateParams";
import { useDateParams } from "~/stores/searchParams/dateParams";
import { usePostHogStore } from "~/stores/usePostHogStore";
import { DocumentKind } from "~/types";

export { DateSearchMode } from "~/stores/searchParams/dateParams";

export type DocumentKindSelectable = Omit<DocumentKind, DocumentKind.All>;

export interface QueryParams {
  query: string;
  category: string;
  itemsPerPage: number;
  pageNumber: number;
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
  Object.entries(query).forEach(([key, value]) => {
    if (value) {
      result[key] = value.toString();
    }
  });
  return result;
}

export const useSimpleSearchParamsStore = defineStore(
  "simpleSearchParams",
  () => {
    const router = useRouter();
    const route = useRoute();
    const initialState = getInitialState(route.query);

    /*
        #############
        #   state   #
        #############
    */
    const query = ref(initialState.query);
    const pageNumber = ref(initialState.pageNumber);
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
        pageNumber: pageNumber.value,
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
        pageNumber.value = 0; // reset page number
      },
    });

    /*
        ##############
        #  actions  #
        ##############
    */
    const setQuery = (value: string) => (query.value = value);
    const setPageNumber = (value: number) => (pageNumber.value = value);
    const setItemsPerPage = (value: number) => (itemsPerPage.value = value);
    const setSort = (value: string) => (sort.value = value);

    function $reset() {
      reinitializeFromQuery(route.query);
    }

    function reinitializeFromQuery(routerQuery: LocationQuery) {
      const initialState = getInitialState(routerQuery);

      query.value = initialState.query;
      pageNumber.value = initialState.pageNumber;
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
     track what query has been set by the store, in order to prevent infinite
     update loops
    */
    const storeQuery = ref(normalizeQuery(omitDefaults(params.value)));

    const postHogStore = usePostHogStore();
    const updateRouterQuery = (router: Router, params: LocationQueryRaw) => {
      const query = omitDefaults(params);
      const previousQuery = storeQuery.value;
      postHogStore.searchPerformed(
        "simple",
        addDefaults(query),
        addDefaults(previousQuery),
      );
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
      pageNumber,
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
      $reset,
    };
  },
);
