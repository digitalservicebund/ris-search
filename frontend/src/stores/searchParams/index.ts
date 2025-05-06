import { defineStore } from "pinia";
import { DocumentKind } from "@/types";
import type { DateSearchMode } from "@/stores/searchParams/dateParams";
import { useDateParams } from "@/stores/searchParams/dateParams";
import {
  defaultParams,
  getInitialState,
  omitDefaults,
} from "./getInitialState";
import { ref, watch, computed } from "vue";
import { sortMode } from "@/components/types";
import type { LocationQuery } from "#vue-router";
import _ from "lodash";
import type { LocationQueryRaw, Router } from "vue-router";

export { DateSearchMode } from "@/stores/searchParams/dateParams";

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
      dateParams.$reset();
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
    const routerQuerySetByStore = ref(omitDefaults(params.value));

    const postHogStore = usePostHogStore();
    const updateRouterQuery = (router: Router, params: LocationQueryRaw) => {
      const query = omitDefaults(params);
      const oldQuery = routerQuerySetByStore.value;
      postHogStore.searchPerformed("simple", query, oldQuery);
      routerQuerySetByStore.value = query;
      return router.push({
        ...route,
        query,
      });
    };

    watch(params, () => updateRouterQuery(router, params.value));

    watch(
      () => route.query,
      async (newQuery) => {
        // check whether the store already has the values, in order to prevent loops
        const updatedQueryWasSetByStore = _.isEqualWith(
          toRaw(routerQuerySetByStore.value),
          newQuery,
          (a, b) => a.toString() == b, // numbers in query are represented as strings
        );

        if (!updatedQueryWasSetByStore) {
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
