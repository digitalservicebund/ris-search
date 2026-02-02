import type { LocationQueryValue } from "#vue-router";

type SearchParam = LocationQueryValue | LocationQueryValue[] | undefined;

/**
 * Normalizes a query parameter from the router to a single string value, or
 * undefined if it isn't set.
 *
 * @param param - Query parameter to normalize
 */
export function searchParamToString(param: SearchParam): string | undefined {
  return (Array.isArray(param) ? param[0] : param) ?? undefined;
}

/**
 * Parses a numeric value from a query parameter in the router.
 *
 * @param param - Query parameter to parse
 * @param fallback - Fallback value if the value is empty or fails to parse
 */
export function searchParamToNumber(
  param: SearchParam,
  fallback: number,
): number;
export function searchParamToNumber(
  param: SearchParam,
  fallback?: undefined,
): number | undefined;
export function searchParamToNumber(
  param: SearchParam,
  fallback: number | undefined = undefined,
): number | undefined {
  let result: number | undefined = fallback;
  const asString = searchParamToString(param);

  if (asString) {
    const parsedNumber = Number.parseInt(asString);
    if (Number.isFinite(parsedNumber)) result = parsedNumber;
  }

  return result;
}
