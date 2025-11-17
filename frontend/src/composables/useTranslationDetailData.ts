import type { AsyncData, NuxtError } from "#app";
import { useTranslationListData } from "~/composables/useTranslationListData";

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

function translationHtmlURL(filename: string) {
  return `/v1/translatedLegislation/${filename}`;
}

export function fetchTranslationAndHTML(
  id: string,
): AsyncData<TranslationData, NuxtError | undefined> {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `translation-and-html-${id}`,
    async () => {
      const { translations } = await useTranslationListData(id);

      if (!translations.value || translations.value.length === 0) {
        throw notFoundError("Translation not found");
      }

      const firstTranslationsListElement = translations.value[0];
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
