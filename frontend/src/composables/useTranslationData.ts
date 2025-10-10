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

function throwNotFound(message: string) {
  throw createError({ statusCode: 404, statusMessage: message });
}

function useApi() {
  const requestFetch = useRequestFetch();
  const backendURL = useBackendURL();
  return { requestFetch, backendURL };
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
  NuxtError<TranslationContent> | null
> {
  const { requestFetch, backendURL } = useApi();

  return useAsyncData("translations-list", async () => {
    const response = await requestFetch<TranslationContent[]>(
      translationsListURL(backendURL),
    );
    if (!response || response.length === 0) throwNotFound("Not Found");
    return response;
  });
}

export function fetchTranslationAndHTML(
  id: string,
): AsyncData<TranslationData, NuxtError | null> {
  const { requestFetch, backendURL } = useApi();

  return useAsyncData(`translation-and-html-${id}`, async () => {
    const translationsList = await requestFetch<TranslationContent[]>(
      translationDetailURL(backendURL, id),
    );

    if (!translationsList || translationsList.length === 0)
      throwNotFound("Translation not found");

    const firstTranslationsListElement = translationsList[0];
    const htmlFilename = firstTranslationsListElement["ris:filename"];
    if (!htmlFilename) throwNotFound("Translation filename not found");

    const htmlData = await requestFetch<string>(
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
): AsyncData<SearchResult<LegislationWork> | null, NuxtError | null> {
  const { requestFetch, backendURL } = useApi();

  return useAsyncData(`german-original-${id}`, async () => {
    const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
    const response = await requestFetch<
      JSONLDList<SearchResult<LegislationWork>>
    >(legislationSearchURL(backendURL, id, currentDateInGermanyFormatted));

    if (!response || response.member.length === 0) throwNotFound("Not Found");

    return response.member[0];
  });
}
