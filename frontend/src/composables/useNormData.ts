import type { AsyncData, NuxtError } from "#app";
import type { LegislationWork } from "~/types";
import { getTextFromElements, parseDocument } from "~/utils/htmlParser";

export interface NormContent {
  legislationWork: LegislationWork;
  html: string;
  htmlParts: {
    heading?: string;
    headingAuthorialNotes?: string; // amtliche Fußnoten
    headingAuthorialNotesLength?: number;
    headingNotes?: string; // nichtamtliche Fußnoten
    officialToc?: string;
    prefaceContainer?: string; // besonderer Hinweis
    vollzitat?: string;
    standangaben?: string[];
    standangabenHinweis?: string[];
  };
}

/**
 * Fetches the metadata and actual content for a whole norm as an async operation.
 * This has the advantage of only needing to be processed once, either during
 * client-side or server-side rendering.
 * It will also extract the title and table of contents HTML parts, to be
 * displayed in separately from the main content.
 *
 * @param expressionEli The ELI for the expression to display. The concrete
 * manifestation ELI of the HTML version will be derived from metadata (from
 * first API call).
 */
export function useFetchNormContent(
  expressionEli: string,
): AsyncData<
  NormContent,
  NuxtError<NormContent> | NuxtError<null> | undefined
> {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `json+html for ${expressionEli}`,
    async () => {
      const backendUrl = useBackendUrl();
      const metadata = await $risBackend<LegislationWork>(
        backendUrl + `/v1/legislation/eli/${expressionEli}`,
      );
      const contentUrl = getContentUrl(metadata);

      const html = await $risBackend<string>(backendUrl + contentUrl, {
        headers: {
          Accept: "text/html",
        },
      });
      const document = parseDocument(html);
      const htmlParts = extractHtmlParts(document);

      return {
        legislationWork: metadata,
        html,
        htmlParts,
      };
    },
    { server: true, lazy: false },
  );
}

function extractHtmlParts(document: Document): NormContent["htmlParts"] {
  const heading = document.querySelector(".dokumentenkopf .titel");
  const headingAuthorialNotes = document.querySelector(
    ".dokumentenkopf .fussnoten",
  );

  const headingNotes = document.querySelector(".dokumentenkopf .akn-notes");
  const prefaceContainer = document.querySelector(
    ".dokumentenkopf .akn-container",
  );

  const officialToc = document.querySelector(".official-toc")?.outerHTML;
  const proprietary = document.querySelector(".akn-proprietary");
  const standangaben = getTextFromElements(
    proprietary?.querySelectorAll(
      ".ris-standangabe:not([data-type='hinweis'])",
    ),
  );
  const standangabenHinweis = getTextFromElements(
    proprietary?.querySelectorAll(".ris-standangabe[data-type='hinweis']"),
  );
  const vollzitat =
    proprietary?.querySelector(".ris-vollzitat")?.textContent ?? undefined;

  return {
    officialToc,
    heading: heading?.outerHTML,
    headingAuthorialNotes: headingAuthorialNotes?.outerHTML,
    headingNotes: headingNotes?.outerHTML,
    headingAuthorialNotesLength: headingAuthorialNotes?.textContent?.length,
    prefaceContainer: prefaceContainer?.outerHTML,
    standangaben,
    standangabenHinweis,
    vollzitat,
  };
}

export interface NormArticleContent {
  legislationWork: LegislationWork;
  html: string;
  articleHeading?: string;
}

/**
 * Fetches the metadata and actual content for a single norm article as an async
 * operation.
 * This has the advantage of only needing to be processed once, either during
 * client-side or server-side rendering.
 * It will also extract the article title, to be displayed separately from
 * the main content.
 *
 * @param expressionEli The ELI for the expression to display. The concrete
 * manifestation ELI of the HTML version will be derived from metadata (from
 * first API call).
 * @param articleEId The eId of the single article belonging to the norm
 * indicated by the expressionEli.
 * It will be appended to the manifestation ELI URL when requesting HTML.
 */
export function useFetchNormArticleContent(
  expressionEli: string,
  articleEId?: string,
): AsyncData<
  NormArticleContent,
  NuxtError<NormContent> | NuxtError<null> | undefined
> {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `json+html for ${expressionEli}/${articleEId}`,
    async () => {
      const backendUrl = useBackendUrl();
      const metadata = await $risBackend<LegislationWork>(
        `${backendUrl}/v1/legislation/eli/${expressionEli}`,
      );
      // build the article URL by appending the eId in front of the .html suffix
      const adaptedContentUrl = getContentUrl(metadata).replace(
        /\.html$/,
        `/${articleEId}.html`,
      );
      const html = await $risBackend<string>(backendUrl + adaptedContentUrl, {
        headers: {
          Accept: "text/html",
        },
      });
      const document = parseDocument(html);
      const articleHeading = document.querySelector(
        "h2.einzelvorschrift",
      )?.innerHTML;

      return {
        legislationWork: metadata,
        html,
        articleHeading,
      };
    },
    { immediate: !!articleEId, server: true, lazy: false },
  );
}

/**
 * Extracts the HTML URL from a LegislationWork metadata structure.
 * @throws Error If the metadata doesn't include an HTML version.
 */
function getContentUrl(metadata: LegislationWork) {
  const encoding = metadata?.workExample.encoding?.find(
    (e) => e.encodingFormat === "text/html",
  );
  const contentUrl = encoding?.contentUrl;
  if (contentUrl) {
    console.info("using manifestation", encoding?.["@id"]);
  } else {
    console.error("contentUrl is missing", metadata?.workExample?.encoding);
    throw new Error("contentUrl is missing");
  }

  return contentUrl;
}
