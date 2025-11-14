import type { AsyncData, NuxtError } from "#app";
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
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    "translations-list",
    async () => {
      const response = await $risBackend<TranslationContent[]>(
        translationsListURL(),
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
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `translations-list-with_id-${id}`,
    async () => {
      const response = await $risBackend<TranslationContent[]>(
        translationDetailURL(id),
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
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `translation-and-html-${id}`,
    async () => {
      const translationsList = await $risBackend<TranslationContent[]>(
        translationDetailURL(id),
      );

      if (!translationsList || translationsList.length === 0) {
        throw notFoundError("Translation not found");
      }

      const firstTranslationsListElement = translationsList[0];
      const htmlFilename = firstTranslationsListElement?.["ris:filename"];
      if (htmlFilename === undefined) {
        throw notFoundError("Translation filename not found");
      }

      const htmlData = await $risBackend<string>(
        translationHtmlURL(htmlFilename),
        {
          headers: {
            Accept: "text/html",
          },
        },
      );

      return { content: firstTranslationsListElement, html: htmlData };
    },
    { server: true, lazy: false },
  );
}

export function getGermanOriginal(
  id: string,
): AsyncData<SearchResult<LegislationWork> | null, NuxtError | undefined> {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `german-original-${id}`,
    async () => {
      const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
      const response = await $risBackend<
        JSONLDList<SearchResult<LegislationWork>>
      >(legislationSearchURL(id, currentDateInGermanyFormatted));

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
