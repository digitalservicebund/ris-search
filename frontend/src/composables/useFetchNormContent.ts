import type { AsyncData, NuxtError } from "#app";
import type { LegislationExpression } from "~/types/api";
import { getText, takeElements } from "~/utils/html/extract";
import type { ProcessedHtml } from "~/utils/html/processHtml";
import {
  DANGEROUSLY_SKIP_SANITIZATION,
  processHtml,
} from "~/utils/html/processHtml";
import { parseDocument } from "~/utils/htmlParser";

export type NormContent = {
  legislation: LegislationExpression;
  htmlParts: {
    heading?: string;
    headingAuthorialNotes?: string; // amtliche Fußnoten
    headingAuthorialNotesLength?: number;
    headingNotes?: string; // nichtamtliche Fußnoten
    officialToc?: string;
    prefaceContainer?: string; // besonderer Hinweis
    body: string;
  };
  hasEmptyBody: boolean;
};

export type UseFetchNormContentOptions = {
  rewriteLink: (link: string | null) => string | null;
};

type ExtractHtmlPartsOptions = UseFetchNormContentOptions;

/**
 * Fetches the metadata of a norm exluding its articles content as an async
 * operation. This has the advantage of only needing to be processed once,
 * either during client-side or server-side rendering. It will also extract the
 * title and table of contents HTML parts, to be displayed in separately from
 * the main content.
 *
 * @param expressionEli The ELI for the expression to display. The concrete
 *   manifestation ELI of the HTML version will be derived from metadata (from
 *   first API call).
 */
export function useFetchNormContent(
  expressionEli: string,
  options?: UseFetchNormContentOptions,
): AsyncData<
  NormContent,
  NuxtError<NormContent> | NuxtError<null> | undefined
> {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `json+html for ${expressionEli}`,
    async () => {
      const metadata = await $risBackend<LegislationExpression>(
        `/v1/legislation/eli/${expressionEli}`,
      );
      const contentUrl = getContentUrl(metadata);

      const html = await $risBackend<string>(contentUrl, {
        headers: {
          Accept: "text/html",
        },
      });
      const document = parseDocument(html);
      const htmlParts = extractHtmlParts(document, options);
      return {
        legislation: metadata,
        htmlParts,
        hasEmptyBody: isNormBodyEmpty(document),
      };
    },
    { server: true, lazy: false },
  );
}

function extractHtmlParts(
  document: Document,
  options?: ExtractHtmlPartsOptions,
): NormContent["htmlParts"] {
  const { body } = document;
  if (options?.rewriteLink) {
    const links = body.querySelectorAll("a");
    links.forEach((link) => {
      const newHref = options.rewriteLink(link.getAttribute("href"));
      if (newHref !== null) link.setAttribute("href", newHref);
      else link.removeAttribute("href");
    });
  }

  const heading = document.querySelector(".dokumentenkopf .titel");
  const headingAuthorialNotes = document.querySelector(
    ".dokumentenkopf .fussnoten",
  );
  // Normalize consecutive whitespace to match what users actually see
  const headingAuthorialNotesLength = headingAuthorialNotes?.textContent
    .trim()
    .replaceAll(/\s+/g, " ").length;

  const headingNotes = document.querySelector(
    ".dokumentenkopf .nichtamtliche-fussnoten",
  );

  const prefaceContainer = document.querySelector(
    ".dokumentenkopf .akn-container",
  );

  const officialTocElements = Array.from(
    document.querySelectorAll(
      ".official-toc, .eingangsformel .inhaltsuebersicht .nichtamtliche-fussnoten",
    ),
  );

  const officialToc = officialTocElements.length
    ? officialTocElements.map((el) => el.outerHTML).join("")
    : undefined;

  return {
    officialToc,
    heading: heading?.outerHTML,
    headingAuthorialNotes: headingAuthorialNotes?.outerHTML,
    headingNotes: insertLineBreaksBetweenBracketedBlocks(
      headingNotes?.outerHTML,
    ),
    headingAuthorialNotesLength,
    prefaceContainer: prefaceContainer?.outerHTML,
    body: body.innerHTML,
  };
}

/**
 * Inserts <br> between consecutive "(+++ ... +++)" blocks within footnote HTML
 * so each block appears on its own line.
 */
function insertLineBreaksBetweenBracketedBlocks(
  html: string | undefined,
): string | undefined {
  if (!html) return undefined;
  return html.replaceAll(/\+\+\+\)\s+\(\+\+\+/g, "+++)<br />(+++");
}

export type NormArticleContent = {
  legislation: LegislationExpression;
  html: ProcessedHtml<
    { heading: string | undefined },
    { headline: string | undefined; firstParagraph: string | undefined }
  >;
};

/**
 * Fetches the metadata and actual content for a single norm article as an async
 * operation. This has the advantage of only needing to be processed once,
 * either during client-side or server-side rendering. It will also extract the
 * article title, to be displayed separately from the main content.
 *
 * @param expressionEli The ELI for the expression to display. The concrete
 *   manifestation ELI of the HTML version will be derived from metadata (from
 *   first API call).
 * @param articleEId The eId of the single article belonging to the norm
 *   indicated by the expressionEli. It will be appended to the manifestation
 *   ELI URL when requesting HTML.
 */
export function useFetchNormArticleContent(
  expressionEli: string,
  articleEId: string,
) {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `json+html for ${expressionEli}/${articleEId}`,
    async () => {
      const metadata = await $risBackend<LegislationExpression>(
        `/v1/legislation/eli/${expressionEli}`,
      );
      // build the article URL by appending the eId in front of the .html suffix
      const adaptedContentUrl = getContentUrl(metadata).replace(
        /\.html$/,
        `/${articleEId}.html`,
      );
      const html = await $risBackend<string>(adaptedContentUrl, {
        headers: {
          Accept: "text/html",
        },
      });
      return { legislation: metadata, html: processArticleHtml(html) };
    },
    { immediate: !!articleEId, server: true, lazy: false },
  );
}

function processArticleHtml(html: string): NormArticleContent["html"] {
  return processHtml(html, {
    // TODO RISDEV-11107: replace with an allowlist
    allowlist: DANGEROUSLY_SKIP_SANITIZATION,
    extract: extractArticleParts,
  });
}

function extractArticleParts(document: Document) {
  const headline = getText(document, "h2.einzelvorschrift")[0];
  const firstParagraph = getText(document, "p")[0];

  // The page shows the heading inside its own title element, so only the
  // content of the heading is needed, not the element itself
  const heading = takeElements(document, "h2.einzelvorschrift", {
    innerHtml: true,
  });

  return { html: { heading }, text: { headline, firstParagraph } };
}

/**
 * Extracts the HTML URL from a Legislation metadata structure.
 *
 * @throws Error If the metadata doesn't include an HTML version.
 */
function getContentUrl(metadata: LegislationExpression) {
  const encoding = metadata?.encoding?.find(
    (e) => e.encodingFormat === "text/html",
  );

  const contentUrl = encoding?.contentUrl;

  if (!contentUrl) {
    throw new Error("contentUrl is missing", {
      cause: { encoding: metadata?.encoding },
    });
  }

  return contentUrl;
}
