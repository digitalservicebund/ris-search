import type { DocumentKind } from "~/types";

const filterTypes = [
  "allTime",
  "specificDate",
  "period",
  "currentlyInForce",
] as const;

/** Different ways of filtering by date */
export type FilterType = (typeof filterTypes)[number];

/** Active date filter */
export type DateFilterValue = {
  /** The type of the filter */
  type: FilterType;
  /** Start date of the filter if applicable */
  from?: string;
  /** End date of the filter if applicable */
  to?: string;
};

/**
 * Checks if an arbitrary string matches one of the known filter types.
 *
 * @param maybe String to check
 * @returns True if the string is a filter type
 */
export function isFilterType(maybe: string): maybe is FilterType {
  return filterTypes.includes(maybe as FilterType);
}

/**
 * Converts a date filter to a Lucene query string.
 *
 * @param filter Date filter to convert
 * @returns Lucene query string or undefined if the filter is "allTime"
 */
export function dateFilterToQuery(
  filter: DateFilterValue,
  _documentKind: DocumentKind,
): string | undefined {
  if (filter.type === "allTime") return undefined;
}
