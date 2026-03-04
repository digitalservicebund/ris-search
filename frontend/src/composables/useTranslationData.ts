import type { AsyncData, NuxtError } from "#app";
import type { JSONLDList, LegislationExpression, SearchResult } from "~/types";
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
  return `/v1/translatedLegislation`;
}

function translationDetailURL(id: string) {
  return `/v1/translatedLegislation?id=${id}`;
}

function translationHtmlURL(filename: string) {
  return `/v1/translatedLegislation/${filename}`;
}

function legislationSearchURL(id: string, currentDate: string) {
  return `/v1/legislation?searchTerm=${id}&temporalCoverageFrom=${currentDate}&temporalCoverageTo=${currentDate}&size=100&pageIndex=0`;
}

export async function fetchTranslationList() {
  const { data, error, status, pending, execute } = await useRisBackend<
    TranslationContent[]
  >(translationsListURL());
  return {
    translations: data,
    translationsError: error,
    translationsIsPending: pending,
    translationsStatus: status,
    executeTranslations: execute,
  };
}

export async function fetchTranslationListWithIdFilter(id: string) {
  const { data, error, status, pending, execute } = await useRisBackend<
    TranslationContent[]
  >(translationDetailURL(id));
  return {
    translations: data,
    translationsError: error,
    translationsIsPending: pending,
    translationsStatus: status,
    executeTranslations: execute,
  };
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

export async function getGermanOriginal(id: string) {
  const currentDateInGermanyFormatted = getCurrentDateInGermanyFormatted();
  const searchEndpoint = legislationSearchURL(
    id,
    currentDateInGermanyFormatted,
  );

  const legislation = ref<SearchResult<LegislationExpression> | null>(null);
  const legislationSearchError = ref<Error | null>(null);
  const legislationSearchStatus = ref<string | null>(null);

  const { data, error, status, pending, execute } =
    await useRisBackend<JSONLDList<SearchResult<LegislationExpression>>>(
      searchEndpoint,
    );

  legislationSearchStatus.value = status.value;

  if (data.value?.totalItems === 0 || data.value == null) {
    legislation.value = null;
    legislationSearchError.value = new Error(`No results found for ${id}`);
    legislationSearchStatus.value = "404";
  } else if (error.value) {
    legislationSearchError.value = error.value;
  } else {
    const firstResult = data.value?.member?.[0] ?? null;
    if (firstResult?.item?.abbreviation === id) {
      legislation.value = firstResult;
    } else {
      legislationSearchError.value = new Error(
        `The fetched legislation does not match the requested ID: ${id}`,
      );
      legislationSearchStatus.value = "404";
    }
  }

  return {
    legislation,
    legislationSearchError,
    legislationSearchIsPending: pending,
    legislationSearchStatus,
    executeLegislationSearch: execute,
  };
}
