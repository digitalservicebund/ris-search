import type { JSDOM as JSDOMType } from "jsdom";

const JSDOM: typeof JSDOMType | undefined = import.meta.server
  ? (await import("jsdom")).JSDOM
  : undefined;

export function parseDocument(html: string): Document {
  if (import.meta.client) {
    // Client-side: use DOMParser
    const parser = new DOMParser();
    return parser.parseFromString(html, "text/html");
  }

  /* v8 ignore next 4 */
  if (!JSDOM) {
    throw new Error("jsdom is not available to parse HTML on the server");
  }
  return new JSDOM(html).window.document;
}

/**
 * Determines if the given html document is empty. A document is considered
 * empty if the html `<body>` is empty or contains only a single headline
 * (`<h1>`).
 *
 * @param document
 */
export function isDocumentEmpty(document?: Document): boolean {
  if (!document) {
    return true;
  }

  const bodyElements = Array.from(document.body.children);

  const isEmptyBody =
    bodyElements.length === 0 && !document.body.textContent?.trim();
  const hasSingleH1 =
    bodyElements.length === 1 && bodyElements[0]?.tagName === "H1";
  return isEmptyBody || hasSingleH1;
}
