import type { Page } from "@/components/Pagination/Pagination";
import { axiosInstance } from "@/services/httpClient";
import { DocumentKind } from "@/types";
import type { AxiosResponse } from "axios";
import type { QueryParams } from "~/stores/searchParams";

const timeout = 10000;

export function getUrl(category?: string) {
  if (category?.startsWith(DocumentKind.CaseLaw)) {
    return `/v1/case-law`;
  } else if (category?.startsWith(DocumentKind.Norm)) {
    return `/v1/legislation`;
  } else {
    return `/v1/document`;
  }
}

function getAdvancedSearchUrl(documentKind: DocumentKind) {
  if (documentKind === DocumentKind.CaseLaw) {
    return `/v1/document/lucene-search/case-law`;
  } else if (documentKind === DocumentKind.Norm) {
    return `/v1/document/lucene-search/legislation`;
  } else {
    return `/v1/document/lucene-search`;
  }
}

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
    const stripped = params.category.substring(2); // remove "R." prefix
    result["typeGroup"] = stripped;
  }
  if (params.temporalCoverage) {
    // temporalCoverage is expected as a range
    result["temporalCoverageFrom"] = params.temporalCoverage;
    result["temporalCoverageTo"] = params.temporalCoverage;
  }
  return result;
}

export async function search(params: {
  query: string;
  itemsPerPage: number;
  pageNumber: number;
  sort: string;
  category?: string;
  date?: string;
  dateAfter?: string;
  dateBefore?: string;
  court?: string;
  temporalCoverage?: string;
}): Promise<AxiosResponse<Page>> {
  const convertedParams = new URLSearchParams({
    searchTerm: params.query,
    size: params.itemsPerPage.toString(),
    pageIndex: params.pageNumber.toString(),
    sort: params.sort,
  });
  if (params.date) {
    convertedParams.set("dateFrom", params.date);
    convertedParams.set("dateTo", params.date);
  }
  if (params.dateAfter) {
    convertedParams.set("dateFrom", params.dateAfter);
  }
  if (params.dateBefore) {
    convertedParams.set("dateTo", params.dateBefore);
  }
  if (params.category && params.category.length > 2) {
    const stripped = params.category.substring(2); // remove "R." prefix
    convertedParams.append("typeGroup", stripped);
  }
  if (params.court) {
    convertedParams.set("court", params.court);
  }
  if (params.temporalCoverage) {
    // temporalCoverage is expected as a range
    convertedParams.set("temporalCoverageFrom", params.temporalCoverage);
    convertedParams.set("temporalCoverageTo", params.temporalCoverage);
  }

  const path = getUrl(params.category);
  const url = `${path}?${convertedParams}`;
  const config = useRuntimeConfig();
  const baseURL = config.public.backendURL;

  return await axiosInstance.get(url, {
    timeout,
    headers: {
      Accept: "application/json",
    },
    baseURL,
  });
}

export async function advancedSearch(params: {
  query: string;
  itemsPerPage: number;
  pageNumber: number;
  sort: string;
  documentKind: DocumentKind;
}) {
  const convertedParams = new URLSearchParams({
    query: params.query,
    size: params.itemsPerPage.toString(),
    pageIndex: params.pageNumber.toString(),
    sort: params.sort,
  });
  const url = `${getAdvancedSearchUrl(params.documentKind)}?${convertedParams}`;

  const config = useRuntimeConfig();
  const baseURL = config.public.backendURL;
  return await axiosInstance.get(url, {
    timeout,
    headers: {
      Accept: "application/json",
    },
    baseURL,
  });
}
