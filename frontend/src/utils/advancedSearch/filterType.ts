import dayjs from "dayjs";
import { DocumentKind } from "~/types";

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

type StrictDateFilterValue =
  | { type: "allTime" | "currentlyInForce"; to: undefined; from: undefined }
  | { type: "specificDate"; to: undefined; from: string }
  | { type: "period"; to: string; from: string };

/**
 * Takes a date filter from the UI and makes sure that its internal state is
 * consistent based on the type:
 *
 * - if the filter contains properties that aren't needed for the type,
 *   removes them
 * - if the filter is missing required information based on the type,
 *   throws an error
 *
 * @param filter Filter value to check
 * @returns Strict date filter
 */
function validateDateFilterValue(
  filter: DateFilterValue,
): StrictDateFilterValue {
  const filterType = filter.type;
  let { from, to } = filter;

  if (filterType === "allTime" || filterType === "currentlyInForce") {
    from = undefined;
    to = undefined;
  } else if (filterType === "specificDate") {
    if (!from) {
      throw new Error(`Missing 'from' date in filter type ${filterType}`, {
        cause: filter,
      });
    }
  } else if (filterType === "period") {
    if (!(from && to)) {
      throw new Error(
        `Missing 'from' or 'to' date in filter type ${filterType}`,
        { cause: filter },
      );
    }
  }

  return { type: filterType, from, to } as StrictDateFilterValue;
}

/**
 * Validates that the date filter value is internally consistent based on the type
 * without returning the result or throwing errors.
 *
 * @param candidate Date filter to check
 * @returns True if the date filter is internally consistent
 */
export function isStrictDateFilterValue(
  candidate: DateFilterValue,
): candidate is StrictDateFilterValue {
  try {
    validateDateFilterValue(candidate);
    return true;
  } catch {
    return false;
  }
}

function validAtPointInTime(pointInTime: string): string {
  return `entry_into_force_date:<${pointInTime} AND ((expiry_date:>${pointInTime}) OR (NOT _exists_:expiry_date))`;
}

/**
 * Converts a date filter to a Lucene query string.
 *
 * @param strictFilter Date filter to convert
 * @returns Lucene query string or undefined if the filter is "allTime"
 */
export function dateFilterToQuery(
  filter: DateFilterValue,
  documentKind: DocumentKind,
): string | undefined {
  if (filter.type === "allTime") return undefined;

  let filterStr: string | undefined = undefined;

  const strictFilter = validateDateFilterValue(filter);

  // Norms
  if (documentKind === DocumentKind.Norm) {
    if (strictFilter.type === "currentlyInForce") {
      filterStr = validAtPointInTime(dayjs().format("YYYY-MM-DD"));
    } else if (strictFilter.type === "period") {
      filterStr = `((expiry_date:>=${strictFilter.from} OR (NOT _exists_:expiry_date))) AND (entry_into_force_date:<=${strictFilter.to})`;
    } else if (strictFilter.type === "specificDate") {
      filterStr = validAtPointInTime(strictFilter.from);
    }
  }

  // Case law
  else if (documentKind === DocumentKind.CaseLaw) {
    if (strictFilter.type === "period") {
      filterStr = `DATUM:[${strictFilter.from} TO ${strictFilter.to}]`;
    } else if (strictFilter.type === "specificDate") {
      filterStr = `DATUM:${strictFilter.from}`;
    }
  }

  return filterStr;
}
