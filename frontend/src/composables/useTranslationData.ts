import type { AsyncData, NuxtError } from "#app";
import type {
  JSONLDList,
  LegislationExpression,
  SearchResult,
} from "~/types/api";
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
  htmlBody: string;
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
  const { data } = await useRisBackend<TranslationContent[]>(
    translationsListURL(),
  );
  return { translations: data };
}

export async function fetchTranslationListWithIdFilter(id: string) {
  const { data } = await useRisBackend<TranslationContent[]>(
    translationDetailURL(id),
  );
  return { translations: data };
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

      const document = parseDocument(htmlData);
      return {
        content: firstTranslationsListElement,
        htmlBody: document.body.innerHTML,
      };
    },
    { server: true, lazy: false },
  );
}

/**
 * Looks up the German original of a translation.
 *
 * Deliberately does not throw: a translation without a matching German norm is
 * a normal state, and so is a failed lookup — the link to the original is a
 * supplement to the page, not part of it. Both cases return null, and the
 * caller simply doesn't render the link.
 *
 * @param id - Abbreviation of the translated norm, e.g. "BGB"
 * @returns The matching German original, or null if there isn't one
 */
export async function getGermanOriginal(id: string) {
  const { data } = await useRisBackend<
    JSONLDList<SearchResult<LegislationExpression>>
  >(legislationSearchURL(id, getCurrentDateInGermanyFormatted()));

  const firstResult = data.value?.member?.[0];
  return firstResult?.item?.abbreviation === id ? firstResult : null;
}
