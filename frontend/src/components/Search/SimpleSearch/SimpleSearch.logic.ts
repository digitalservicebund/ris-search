import { useBackendURL } from "~/composables/useBackendURL";
import type { QueryParams } from "~/stores/searchParams";
import { DocumentKind } from "~/types";

export function getUrl(category?: string): string {
  const backendURL = useBackendURL();
  const routes: [DocumentKind, string][] = [
    [DocumentKind.CaseLaw, "case-law"],
    [DocumentKind.Norm, "legislation"],
    [DocumentKind.Literature, "literature"],
  ];

  const match = routes.find(([kind]) => category?.startsWith(kind));

  return `${backendURL}/v1/${match?.[1] ?? "document"}`;
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
