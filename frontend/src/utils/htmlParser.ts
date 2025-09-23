import type { JSDOM } from "jsdom";

type Section = {
  id: string;
  title: string;
};
let _JSDOM: typeof JSDOM;
if (import.meta.server) {
  _JSDOM = await import("jsdom");
}

export function parseDocument(html: string): Document {
  if (import.meta.client) {
    // Client-side: use DOMParser
    const parser = new DOMParser();
    return parser.parseFromString(html, "text/html");
  } else {
    /* v8 ignore next 2 */
    const dom = new _JSDOM(html);
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
