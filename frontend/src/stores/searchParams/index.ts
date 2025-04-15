import { defineStore } from "pinia";
import { DocumentKind } from "@/types";
import type { DateSearchMode } from "@/stores/searchParams/dateParams";
import { useDateParams } from "@/stores/searchParams/dateParams";
import {
  defaultParams,
  getInitialState,
  setRouterQuery,
} from "./getInitialState";
import { ref, watch, computed } from "vue";
import { sortMode } from "@/components/types";

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
    const initialState = getInitialState(router);

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
      const initialState = getInitialState(router);

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
    watch(params, () => setRouterQuery(router, params.value));

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
