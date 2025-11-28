import { ref, computed } from "vue";
import type { QueryParams } from "./useSimpleSearchParams";
import type { LocationQuery } from "#vue-router";

export enum DateSearchMode {
  None = "",
  Equal = "eq",
  After = "after",
  Before = "before",
  Range = "range",
}

export function dateSearchFromQuery(
  query: LocationQuery,
): Pick<QueryParams, "date" | "dateAfter" | "dateBefore" | "dateSearchMode"> {
  if (query.date) {
    return { dateSearchMode: DateSearchMode.Equal, date: query.date as string };
  } else if (query.dateAfter && query.dateBefore) {
    return {
      dateSearchMode: DateSearchMode.Range,
      dateAfter: query.dateAfter as string,
      dateBefore: query.dateBefore as string,
    };
  } else if (query.dateAfter) {
    return {
      dateSearchMode: DateSearchMode.After,
      dateAfter: query.dateAfter as string,
    };
  } else if (query.dateBefore) {
    return {
      dateSearchMode: DateSearchMode.Before,
      dateBefore: query.dateBefore as string,
    };
  } else {
    return { dateSearchMode: DateSearchMode.None };
  }
}

export function useDateParams(initialState: QueryParams) {
  const date = ref(initialState.date);
  const dateAfter = ref(initialState.dateAfter);
  const dateBefore = ref(initialState.dateBefore);

  const _dateSearchMode = ref(initialState.dateSearchMode);
  const dateSearchMode = computed({
    get: () => _dateSearchMode.value,
    set: (newValue) => {
      const shouldKeepDate = newValue === DateSearchMode.Equal;
      const shouldKeepDateAfter =
        newValue === DateSearchMode.After || newValue === DateSearchMode.Range;
      const shouldKeepDateBefore =
        newValue === DateSearchMode.Before || newValue === DateSearchMode.Range;
      const _date = date.value;
      const _dateAfter = dateAfter.value;
      const _dateBefore = dateBefore.value;

      date.value = shouldKeepDate
        ? (_date ?? _dateAfter ?? _dateBefore)
        : undefined;
      dateAfter.value = shouldKeepDateAfter
        ? (_dateAfter ?? _date ?? _dateBefore)
        : undefined;
      dateBefore.value = shouldKeepDateBefore
        ? (_dateBefore ?? _date ?? _dateAfter)
        : undefined;
      _dateSearchMode.value = newValue;
    },
  });

  function reset(state: QueryParams) {
    date.value = state.date;
    dateAfter.value = state.dateAfter;
    dateBefore.value = state.dateBefore;
    dateSearchMode.value = state.dateSearchMode;
  }

  return { date, dateAfter, dateBefore, dateSearchMode, reset };
}
