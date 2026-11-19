import type { Section } from "~/utils/html/extract";
import { getSections, getText } from "~/utils/html/extract";
import type { ProcessedHtml } from "~/utils/html/processHtml";
import {
  DANGEROUSLY_SKIP_SANITIZATION,
  EMPTY_DOCUMENT_HTML,
  processHtml,
} from "~/utils/html/processHtml";

export type DocumentHtml = ProcessedHtml<
  Record<never, never>,
  { sections: Section[]; firstParagraph: string | undefined }
>;

/**
 * Fetches the HTML of a document.
 *
 * @param url The URL of the HTML version of the document
 */
export function useDocumentHtml(url: string) {
  const { $risBackend } = useNuxtApp();

  return useAsyncData(
    `html for ${url}`,
    async () => {
      const html = await $risBackend<string>(url, {
        headers: { Accept: "text/html" },
      });
      return processDocumentHtml(html);
    },
    { server: true, lazy: false },
  );
}

function processDocumentHtml(html: string): DocumentHtml {
  return processHtml(html || EMPTY_DOCUMENT_HTML, {
    // TODO RISDEV-11107: replace with an allowlist
    allowlist: DANGEROUSLY_SKIP_SANITIZATION,
    extract: extractDocumentParts,
  });
}

function extractDocumentParts(document: Document) {
  const sections = getSections(document, "section");
  const firstParagraph = getText(document, "section p")[0];
  return { html: {}, text: { sections, firstParagraph } };
}
