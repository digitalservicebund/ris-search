import type { QueryParams } from "~/stores/searchParams";
import { DocumentKind } from "~/types";

export function getUrl(category?: string) {
  if (category?.startsWith(DocumentKind.CaseLaw)) {
    return `/v1/case-law`;
  } else if (category?.startsWith(DocumentKind.Norm)) {
    return `/v1/legislation`;
  } else {
    return `/v1/document`;
  }
}

/**
 * Converts the params used in the pinia store to URL params as expected by
 * the API.
 */
export function convertParams(
  params: Omit<QueryParams, "dateSearchMode"> & { temporalCoverage?: string },
) {
  const result: Record<string, string | string[]> = {
    searchTerm: params.query,
    size: params.itemsPerPage.toString(),
    pageIndex: params.pageNumber.toString(),
    sort: params.sort,
  };
  if (params.court) {
    result.court = params.court;
  }
  if (params.date) {
    result["dateFrom"] = params.date;
    result["dateTo"] = params.date;
  }
  if (params.dateAfter) {
    result["dateFrom"] = params.dateAfter;
  }
  if (params.dateBefore) {
    result["dateTo"] = params.dateBefore;
  }
  if (params.category && params.category.length > 2) {
    result["typeGroup"] = params.category.substring(2); // remove "R." prefix
  }
  if (params.temporalCoverage) {
    // temporalCoverage is expected as a range
    result["temporalCoverageFrom"] = params.temporalCoverage;
    result["temporalCoverageTo"] = params.temporalCoverage;
  }
  return result;
}
