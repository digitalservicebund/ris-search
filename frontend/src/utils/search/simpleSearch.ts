import type { QueryParams } from "~/composables/useSimpleSearchParams/useSimpleSearchParams";
import { DocumentKind } from "~/types";

export function getUrl(category?: string): string {
  const routes: [DocumentKind, string][] = [
    [DocumentKind.CaseLaw, "case-law"],
    [DocumentKind.Norm, "legislation"],
    [DocumentKind.Literature, "literature"],
  ];

  const match = routes.find(([kind]) => category?.startsWith(kind));

  return `/v1/${match?.[1] ?? "document"}`;
}

export function categoryToDocumentKind(category: string) {
  if (category.length > 0) {
    return category[0] as DocumentKind;
  }

  return DocumentKind.All;
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
  mostRelevantOn?: string;
}

export function convertParams(
  params: Omit<QueryParams, "dateSearchMode">,
): SearchEndpointParams {
  const documentKind = categoryToDocumentKind(params.category);

  const result: SearchEndpointParams = {
    searchTerm: params.query,
    size: params.itemsPerPage.toString(),
    pageIndex: params.pageNumber.toString(),
    sort: params.sort,
  };

  // Params valid for all document kinds
  if (params.date) {
    result.dateFrom = params.date;
    result.dateTo = params.date;
  }

  if (params.dateAfter) result.dateFrom = params.dateAfter;
  if (params.dateBefore) result.dateTo = params.dateBefore;

  // Params that are only case-law specific
  if (
    documentKind == DocumentKind.All ||
    documentKind == DocumentKind.CaseLaw
  ) {
    if (params.category && params.category.length > 2) {
      result.typeGroup = params.category.substring(2); // remove "R." prefix
    }
    if (params.court) result.court = params.court;
  }

  // Params that are only norm specific
  if (documentKind == DocumentKind.All || documentKind == DocumentKind.Norm) {
    result.mostRelevantOn = getCurrentDateInGermanyFormatted();
  }

  return result;
}
