import { DocumentKind } from "~/types/api";
import {
  dateFilterToQuery,
  type DateFilterValue,
} from "~/utils/search/dateFilterType";

/**
 * Returns the Lucene search endpoint for a document kind.
 *
 * @param documentKind Kind of documents to search for
 * @returns Path of the matching endpoint
 */
export function getLuceneSearchPath(documentKind: DocumentKind): string {
  const baseUrl = "/v1/document/lucene-search";

  if (documentKind === DocumentKind.CaseLaw) {
    return baseUrl + "/case-law";
  } else if (documentKind === DocumentKind.Norm) {
    return baseUrl + "/legislation";
  } else if (documentKind === DocumentKind.Literature) {
    return baseUrl + "/literature";
  } else if (documentKind === DocumentKind.AdministrativeDirective) {
    return baseUrl + "/administrative-directive";
  } else return baseUrl;
}

/**
 * Combines a user query and a date filter into a single Lucene query.
 *
 * @param query Lucene query as entered by the user
 * @param dateFilter Date filter to apply, if any
 * @param documentKind Kind of documents to search for
 * @returns Combined Lucene query, or an empty string if there is nothing to
 *   filter by
 */
export function buildLuceneQuery(
  query: string,
  dateFilter: DateFilterValue | undefined,
  documentKind: DocumentKind,
): string {
  const parts = [query];

  if (dateFilter) {
    const dateQuery = dateFilterToQuery(dateFilter, documentKind);
    if (dateQuery) parts.push(dateQuery);
  }

  return parts
    .filter((i) => !!i.trim())
    .map((i) => `(${i.trim()})`)
    .join(" AND ");
}
