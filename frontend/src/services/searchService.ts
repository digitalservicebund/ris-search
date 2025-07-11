import { axiosInstance } from "~/services/httpClient";
import { DocumentKind } from "~/types";

const timeout = 10000;

function getAdvancedSearchUrl(documentKind: DocumentKind) {
  if (documentKind === DocumentKind.CaseLaw) {
    return `/v1/document/lucene-search/case-law`;
  } else if (documentKind === DocumentKind.Norm) {
    return `/v1/document/lucene-search/legislation`;
  } else {
    return `/v1/document/lucene-search`;
  }
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
