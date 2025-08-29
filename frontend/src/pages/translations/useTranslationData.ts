import type { AsyncData, NuxtError } from "#app";
import { useBackendURL } from "~/composables/useBackendURL";

export interface TranslationContent {
  "@id": string;
  name: string;
  inLanguage: string;
  translator: string;
  translationOfWork?: string;
  about: string;
  "ris:filename": string;
}

export function fetchTranslationList(
  id?: string,
): AsyncData<TranslationContent[], NuxtError<TranslationContent> | null> {
  const requestFetch = useRequestFetch();
  const backendURL = useBackendURL();

  return useAsyncData(`json for translations`, async () => {
    const url = id
      ? `${backendURL}/v1/translatedLegislation?id=${id}`
      : `${backendURL}/v1/translatedLegislation`;
    const res = await requestFetch<TranslationContent[]>(url);
    if (!res || res.length === 0) {
      throw createError({ statusCode: 404, statusMessage: "Not Found" });
    }
    return res;
  });
}

export function fetchTranslationAndHTML(
  id: string,
): AsyncData<{ content: TranslationContent; html: string }, NuxtError | null> {
  const requestFetch = useRequestFetch();
  const backendURL = useBackendURL();

  return useAsyncData(`translation-and-html-${id}`, async () => {
    const translationUrl = `${backendURL}/v1/translatedLegislation?id=${id}`;
    const translationData =
      await requestFetch<TranslationContent[]>(translationUrl);

    if (!translationData || translationData.length === 0) {
      throw createError({
        statusCode: 404,
        statusMessage: "Translation not found",
      });
    }

    const translationContent = translationData[0];

    if (!translationContent["ris:filename"]) {
      throw createError({
        statusCode: 404,
        statusMessage: "Translation filename not found",
      });
    }

    const htmlUrl = `${backendURL}/v1/translatedLegislation/${translationContent["ris:filename"]}`;
    const htmlData = await requestFetch<string>(htmlUrl, {
      headers: {
        Accept: "text/html",
      },
    });

    return {
      content: translationContent,
      html: htmlData,
    };
  });
}
