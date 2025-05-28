import type { QueryParams } from "~/stores/searchParams";
import { DocumentKind } from "~/types";
import { useBackendURL } from "~/composables/useBackendURL";

export function getUrl(category?: string): string {
  const backendURL = useBackendURL();
  if (category?.startsWith(DocumentKind.CaseLaw)) {
    // covers cases such as R and R.urteil
    return `${backendURL}/v1/case-law`;
  } else if (category?.startsWith(DocumentKind.Norm)) {
    return `${backendURL}/v1/legislation`;
  } else {
    return `${backendURL}/v1/document`;
  }
}

export interface SearchEndpointParams {
  searchTerm: string;
  size: string;
  pageIndex: string;
  sort: string;
  court?: string;
  dateFrom?: string;
  dateTo?: string;
  typeGroup?: string;
  temporalCoverageFrom?: string;
  temporalCoverageTo?: string;
}

export function convertParams(
  params: Omit<QueryParams, "dateSearchMode"> & { temporalCoverage?: string },
): SearchEndpointParams {
  const result: SearchEndpointParams = {
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
