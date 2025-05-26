import type { LegislationWork } from "@/types";
import type { AsyncData, NuxtError } from "#app";
import { getTextFromElements } from "~/utils/htmlParser";
import { useBackendURL } from "~/composables/useBackendURL";

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
): AsyncData<NormContent, NuxtError<NormContent> | null> {
  const requestFetch = useRequestFetch(); // unlike $fetch, useRequestFetch forwards client cookies
  return useAsyncData(`json+html for ${expressionEli}`, async () => {
    const backendURL = useBackendURL();
    const metadata = await requestFetch<LegislationWork>(
      `${backendURL}/v1/legislation/eli/${expressionEli}`,
    );
    const contentUrl = backendURL + getContentUrl(metadata);

    const html = await requestFetch<string>(contentUrl, {
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
  });
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
): AsyncData<NormArticleContent, NuxtError<NormContent> | null> {
  const requestFetch = useRequestFetch(); // unlike $fetch, useRequestFetch forwards client cookies
  const backendURL = useBackendURL();
  return useAsyncData(
    `json+html for ${expressionEli}/${articleEId}`,
    async () => {
      const metadata = await requestFetch<LegislationWork>(
        `${backendURL}/v1/legislation/eli/${expressionEli}`,
      );
      // build the article URL by appending the eId in front of the .html suffix
      const adaptedContentUrl =
        backendURL +
        getContentUrl(metadata).replace(/\.html$/, `/${articleEId}.html`);
      const html = await requestFetch<string>(adaptedContentUrl, {
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
    { immediate: !!articleEId },
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
  if (!contentUrl) {
    console.error("contentUrl is missing", metadata?.workExample?.encoding);
    throw new Error("contentUrl is missing");
  } else {
    console.info("using manifestation", encoding?.["@id"]);
  }
  return contentUrl;
}
