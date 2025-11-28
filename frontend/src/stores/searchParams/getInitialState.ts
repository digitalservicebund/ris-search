import _ from "lodash";
import { dateSearchFromQuery, DateSearchMode } from "./dateParams";
import type {
  LocationQuery,
  LocationQueryRaw,
  LocationQueryValue,
} from "#vue-router";
import type { QueryParams } from "~/stores/searchParams/index";
import { DocumentKind } from "~/types";

export const defaultParams: QueryParams = {
  query: "",
  category: DocumentKind.All,
  itemsPerPage: 10,
  pageNumber: 0,
  sort: "default",
  dateSearchMode: DateSearchMode.None,
};

export const omitDefaults = (newValue: LocationQueryRaw): LocationQueryRaw => {
  return _.pickBy(newValue, (value, key) => {
    return (defaultParams as unknown as Record<string, string>)[key] !== value;
  });
};

export const addDefaults = (value: LocationQueryRaw) => {
  return {
    ...defaultParams,
    ...value,
  };
};

const getFirstInt = (
  param: LocationQueryValue | LocationQueryValue[] | undefined,
) => {
  let value: LocationQueryValue | undefined;
  if (Array.isArray(param)) {
    value = param[0];
  } else {
    value = param;
  }
  if (value) return Number.parseInt(value);
};

function getFirstValue(
  value: LocationQueryValue | LocationQueryValue[] | undefined,
): string | undefined {
  return (Array.isArray(value) ? value[0] : value) ?? undefined;
}

export const getInitialState = (routerQuery: LocationQuery): QueryParams => {
  return {
    query: getFirstValue(routerQuery.query) ?? defaultParams.query,
    category: getFirstValue(routerQuery.category) ?? defaultParams.category,
    itemsPerPage:
      getFirstInt(routerQuery.itemsPerPage) ?? defaultParams.itemsPerPage,
    pageNumber: getFirstInt(routerQuery.pageNumber) ?? defaultParams.pageNumber,
    sort: getFirstValue(routerQuery.sort) ?? defaultParams.sort,
    ...dateSearchFromQuery(routerQuery),
    court: getFirstValue(routerQuery.court),
  };
};
