import type { AsyncData, NuxtError } from "#app";
import useBackendUrl from "~/composables/useBackendUrl";
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

function translationsListURL() {
  return useBackendUrl(`/v1/translatedLegislation`);
}

function translationDetailURL(id: string) {
  return useBackendUrl(`/v1/translatedLegislation?id=${id}`);
}

function translationHtmlURL(filename: string) {
  return useBackendUrl(`/v1/translatedLegislation/${filename}`);
}

function legislationSearchURL(id: string, currentDate: string) {
  return useBackendUrl(
    `/v1/legislation?searchTerm=${id}&temporalCoverageFrom=${currentDate}&temporalCoverageTo=${currentDate}&size=100&pageIndex=0`,
  );
}

export function fetchTranslationList(): AsyncData<
  TranslationContent[],
  NuxtError<TranslationContent> | NuxtError<null> | undefined
> {
  return useAsyncData(
    "translations-list",
    async () => {
      const config = useRuntimeConfig();
      const response = await $fetch<TranslationContent[]>(
        translationsListURL(),
        {
          headers: {
            Authorization: `Basic ${config.basicAuth}`,
          },
        },
      );

      if (!response || response.length === 0) throw notFoundError("Not Found");

      return response;
    },
    { server: true, lazy: false },
  );
}

export function fetchTranslationListWithIdFilter(
  id: string,
): AsyncData<
  TranslationContent[],
  NuxtError<TranslationContent> | NuxtError<null> | undefined
> {
  const config = useRuntimeConfig();
  return useAsyncData(
    "translations-list-with_id",
    async () => {
      const response = await $fetch<TranslationContent[]>(
        translationDetailURL(id),
        {
          headers: {
            Authorization: `Basic ${config.basicAuth}`,
          },
        },
      );

      if (!response || response.length === 0) throw notFoundError("Not Found");

      return response;
    },
    { server: true, lazy: false },
  );
}

export function fetchTranslationAndHTML(
  id: string,
): AsyncData<TranslationData, NuxtError | undefined> {
  const config = useRuntimeConfig();
  return useAsyncData(
    `translation-and-html-${id}`,
    async () => {
      const translationsList = await $fetch<TranslationContent[]>(
        translationDetailURL(id),
        {
          headers: {
            Authorization: `Basic ${config.basicAuth}`,
          },
        },
      );

      if (!translationsList || translationsList.length === 0) {
        throw notFoundError("Translation not found");
      }

      const firstTranslationsListElement = translationsList[0];
      const htmlFilename = firstTranslationsListElement?.["ris:filename"];
      if (htmlFilename === undefined) {
        throw notFoundError("Translation filename not found");
      }

      const htmlData = await $fetch<string>(translationHtmlURL(htmlFilename), {
        headers: {
          Accept: "text/html",
          Authorization: `Basic ${config.basicAuth}`,
        },
      });

      return { content: firstTranslationsListElement, html: htmlData };
    },
    { server: true, lazy: false },
  );
}

export function getGermanOriginal(
  id: string,
): AsyncData<SearchResult<LegislationWork> | null, NuxtError | undefined> {
  const config = useRuntimeConfig();
  return useAsyncData(
    `german-original-${id}`,
    async () => {
      const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
      const response = await $fetch<JSONLDList<SearchResult<LegislationWork>>>(
        legislationSearchURL(id, currentDateInGermanyFormatted),
        {
          headers: {
            Authorization: `Basic ${config.basicAuth}`,
          },
        },
      );

      if (!response || response.member.length === 0) {
        throw notFoundError("Not Found");
      }

      const [firstResult] = response.member;

      if (firstResult?.item?.abbreviation === id) {
        return firstResult;
      }
      throw notFoundError(`Not Found: Abbreviation mismatch for ID: ${id}`);
    },
    { server: true, lazy: false },
  );
}
