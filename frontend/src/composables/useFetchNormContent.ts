import type { AsyncData, NuxtError } from "#app";
import type { LegislationExpression } from "~/types/api";
import { getText, takeElements } from "~/utils/html/extract";
import type { ProcessedHtml } from "~/utils/html/processHtml";
import {
  DANGEROUSLY_SKIP_SANITIZATION,
  processHtml,
} from "~/utils/html/processHtml";
import {
  insertLineBreaksBetweenBracketedBlocks,
  rewriteLinks,
} from "~/utils/html/transforms";

export type NormHtml = ProcessedHtml<
  {
    heading: string | undefined;
    /** Amtliche Fußnoten */
    headingAuthorialNotes: string | undefined;
    /** Nichtamtliche Fußnoten */
    headingNotes: string | undefined;
    officialToc: string | undefined;
    /** Besonderer Hinweis */
    prefaceContainer: string | undefined;
  },
  {
    /** Text length of the amtliche Fußnoten, with whitespace as users see it */
    headingAuthorialNotesLength: number;
  }
>;

export type NormContent = {
  legislation: LegislationExpression;
  html: NormHtml;
};

export type UseFetchNormContentOptions = {
  rewriteLink: (link: string | null) => string | null;
};

/**
 * The header of the norm. Attachments have their own header, which is shown as
 * part of the text, so it doesn't count.
 */
const NORM_HEADER = ".dokumentenkopf:not(.akn-attachments *)";

const OFFICIAL_TOC =
  ".official-toc, .eingangsformel .inhaltsuebersicht .nichtamtliche-fussnoten";

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
      return { legislation: metadata, html: processNormHtml(html, options) };
    },
    { server: true, lazy: false },
  );
}

function processNormHtml(
  html: string,
  options?: UseFetchNormContentOptions,
): NormHtml {
  const rewriteLink = options?.rewriteLink;
  const transforms = [
    ...(rewriteLink ? [rewriteLinks((href) => rewriteLink(href))] : []),
    insertLineBreaksBetweenBracketedBlocks(
      `${NORM_HEADER} .nichtamtliche-fussnoten`,
    ),
  ];

  return processHtml(html, {
    // TODO RISDEV-11107: replace with an allowlist
    allowlist: DANGEROUSLY_SKIP_SANITIZATION,
    transforms,
    isEmpty: isNormBodyEmpty,
    extract: extractNormParts,
  });
}

function extractNormParts(document: Document) {
  // Text first, because taking the parts removes the elements it is read from
  const authorialNotesText = getText(document, `${NORM_HEADER} .fussnoten`);
  const text = {
    headingAuthorialNotesLength: normalizeSpaces(authorialNotesText.join(" "))
      .length,
  };

  const html = {
    heading: takeElements(document, `${NORM_HEADER} .titel`),
    headingAuthorialNotes: takeElements(document, `${NORM_HEADER} .fussnoten`),
    headingNotes: takeElements(
      document,
      `${NORM_HEADER} .nichtamtliche-fussnoten`,
    ),
    prefaceContainer: takeElements(document, `${NORM_HEADER} .akn-container`),
    officialToc: takeElements(document, OFFICIAL_TOC),
  };

  // The rest of these elements is shown elsewhere on the page or not at all,
  // e.g. the short title or the heading of the official table of contents
  takeElements(document, NORM_HEADER);
  takeElements(document, ".akn-proprietary");
  takeElements(document, ".eingangsformel .inhaltsuebersicht");

  return { html, text };
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
