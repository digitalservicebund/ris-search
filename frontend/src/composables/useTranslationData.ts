import type { AsyncData, NuxtError } from "#app";
import { useBackendURL } from "~/composables/useBackendURL";
import type { LegislationWork, JSONLDList, SearchResult } from "~/types";
import { getCurrentDateInGermanyFormatted } from "~/utils/dateFormatting";

export interface TranslationContent {
  "@id": string;
  name: string;
  inLanguage: string;
  translator: string;
  translationOfWork?: string;
  about: string;
  "ris:filename": string;
}

export interface TranslationData {
  content: TranslationContent;
  html: string;
}

function notFoundError(message: string) {
  return createError({ statusCode: 404, statusMessage: message });
}

function useApi() {
  const apiFetch = useRequestFetch();
  const backendURL = useBackendURL();
  return { apiFetch, backendURL };
}

function translationsListURL(backendURL: string) {
  return `${backendURL}/v1/translatedLegislation`;
}

function translationDetailURL(backendURL: string, id: string) {
  return `${backendURL}/v1/translatedLegislation?id=${id}`;
}

function translationHtmlURL(backendURL: string, filename: string) {
  return `${backendURL}/v1/translatedLegislation/${filename}`;
}

function legislationSearchURL(
  backendURL: string,
  id: string,
  currentDate: string,
) {
  return `${backendURL}/v1/legislation?searchTerm=${id}&temporalCoverageFrom=${currentDate}&temporalCoverageTo=${currentDate}&size=100&pageIndex=0`;
}

export function fetchTranslationList(): AsyncData<
  TranslationContent[],
  NuxtError<TranslationContent> | NuxtError<null> | undefined
> {
  const { apiFetch, backendURL } = useApi();

  return useAsyncData("translations-list", async () => {
    const response = await apiFetch<TranslationContent[]>(
      translationsListURL(backendURL),
    );

    if (!response || response.length === 0) throw notFoundError("Not Found");

    return response;
  });
}

export function fetchTranslationAndHTML(
  id: string,
): AsyncData<TranslationData, NuxtError | undefined> {
  const { apiFetch, backendURL } = useApi();

  return useAsyncData(`translation-and-html-${id}`, async () => {
    const translationsList = await apiFetch<TranslationContent[]>(
      translationDetailURL(backendURL, id),
    );

    if (!translationsList || translationsList.length === 0) {
      throw notFoundError("Translation not found");
    }

    const firstTranslationsListElement = translationsList[0];
    const htmlFilename = firstTranslationsListElement?.["ris:filename"];
    if (htmlFilename === undefined) {
      throw notFoundError("Translation filename not found");
    }

    const htmlData = await apiFetch<string>(
      translationHtmlURL(backendURL, htmlFilename),
      {
        headers: { Accept: "text/html" },
      },
    );

    return { content: firstTranslationsListElement, html: htmlData };
  });
}

export function getGermanOriginal(
  id: string,
): AsyncData<SearchResult<LegislationWork> | null, NuxtError | undefined> {
  const { apiFetch, backendURL } = useApi();

  return useAsyncData(`german-original-${id}`, async () => {
    const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
    const response = await apiFetch<JSONLDList<SearchResult<LegislationWork>>>(
      legislationSearchURL(backendURL, id, currentDateInGermanyFormatted),
    );

    if (!response || response.member.length === 0) {
      throw notFoundError("Not Found");
    }

    return response.member[0];
  });
}
