import type { JSDOM } from "jsdom";

type Section = {
  id: string;
  title: string;
};
let _JSDOM: typeof JSDOM;
if (import.meta.server) {
  import("jsdom").then((module) => {
    _JSDOM = module.JSDOM;
  });
}

export function parseDocument(html: string): Document {
  const needsWrapper = !/<html[\s>]/i.test(html) && !/<body[\s>]/i.test(html);
  const source = needsWrapper ? `<div>${html}</div>` : html;

  if (import.meta.client) {
    // Client-side: use DOMParser
    const parser = new DOMParser();
    return parser.parseFromString(source, "text/html");
  } else {
    /* v8 ignore next 2 */
    const dom = new _JSDOM(source);
    return dom.window.document;
  }
}

/**
 * Extracts all headings (or specified elements) from HTML content.
 *
 * @param html - The raw HTML string.
 * @param elementName - The element tag name to search for (e.g., "h2", "h3").
 * @returns An array of headings with `id` and `title`.
 */
export function getAllSectionsFromHtml(
  html: string,
  elementName: string,
): Section[] {
  const elements: NodeListOf<Element> =
    parseDocument(html).querySelectorAll(elementName);

  return (Array.from(elements) as HTMLElement[]).map((element) => {
    const heading = element.querySelector("h2");
    return {
      id: element.id,
      title: heading?.textContent ?? "", // Ensure textContent is not null
    };
  });
}

export function getTextFromElements(elements?: NodeListOf<Element>): string[] {
  if (!elements) {
    return [];
  }
  const textElements: (string | null)[] = [...elements.values()].map(
    (element) => element.textContent,
  );
  return textElements.filter(Boolean) as string[];
}

/**
 * Determines if the given html document is empty. A document is considered empty
 * if the html `<body>` is empty or contains only a single headline (`<h1>`).
 * @param htmlDocument
 */
export function isDocumentEmpty(htmlDocument?: string): boolean {
  if (!htmlDocument) {
    return true;
  }

  const doc = parseDocument(htmlDocument);
  const bodyElements = Array.from(doc.body.children);

  const isEmptyBody =
    bodyElements.length === 0 && doc.body.childNodes.length == 0;
  const hasSingleH2 =
    bodyElements.length === 1 && bodyElements[0]?.tagName === "H1";
  return isEmptyBody || hasSingleH2;
}
