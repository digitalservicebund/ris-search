import type { JSDOM } from "jsdom";
import sanitizeHtml from "sanitize-html";

type Section = {
  id: string;
  title: string;
};

let jsdom: typeof JSDOM;
if (import.meta.server) {
  import("jsdom").then((module) => {
    jsdom = module.JSDOM;
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
    const dom = new jsdom(source);
    return dom.window.document;
  }
}

/**
 * Extracts all headings (or specified elements) from HTML Document.
 *
 * @param document - The raw HTML string.
 * @param elementName - The element tag name to search for (e.g., "h2", "h3").
 * @returns An array of headings with `id` and `title`.
 */
export function getAllSectionsFromDocument(
  document: Document,
  elementName: string,
): Section[] {
  const elements: NodeListOf<Element> = document.querySelectorAll(elementName);

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

export type VerweiseGroup = {
  id: string;
  label: string;
  html: string;
};

/**
 * Verweise categories in display order, matching the `<template id="...">`
 * elements the case-law HTML transformation places in `<head>` (one per
 * category, e.g. `referenzNorm`, `vorgehendeEntscheidung`), each containing a
 * `<ul>` of `<li>` entries.
 */
const VERWEISE_CATEGORIES: { id: string; label: string }[] = [
  { id: "referenzNorm", label: "Normen" },
  { id: "referenzRechtsprechungAktiv", label: "Zitierte Rechtsprechung" },
  { id: "referenzRechtsprechungPassiv", label: "Verweisende Rechtsprechung" },
  {
    id: "referenzVerwaltungsvorschriftAktiv",
    label: "Zitierte Verwaltungsvorschriften",
  },
  {
    id: "referenzVerwaltungsvorschriftPassiv",
    label: "Verweisende Verwaltungsvorschriften",
  },
  {
    id: "referenzUnselbstaendigeLiteraturAktiv",
    label: "Zitierte unselbständige Literatur",
  },
  {
    id: "referenzUnselbstaendigeLiteraturPassiv",
    label: "Verweisende unselbständige Literatur",
  },
  {
    id: "referenzSelbstaendigeLiteraturAktiv",
    label: "Zitierte selbständige Literatur",
  },
  {
    id: "referenzSelbstaendigeLiteraturPassiv",
    label: "Verweisende selbständige Literatur",
  },
  { id: "vorgehendeEntscheidung", label: "Vorgehende Entscheidungen" },
  { id: "nachgehendeEntscheidung", label: "Nachgehende Entscheidungen" },
];

/**
 * Extracts the verweise categories present in the document's `<head>`, paired
 * with a display label. Categories without a matching template (or with no
 * content) are omitted. The list markup is sanitized down to `<ul>`, `<li>` and
 * `<a>` elements only.
 */
export function getVerweiseGroups(document: Document): VerweiseGroup[] {
  return VERWEISE_CATEGORIES.flatMap(({ id, label }) => {
    const rawHtml = document.head.querySelector<HTMLTemplateElement>(
      `template#${id}`,
    )?.innerHTML;
    const html =
      rawHtml && sanitizeHtml(rawHtml, { allowedTags: ["ul", "li", "a"] });
    return html ? [{ id, label, html }] : [];
  });
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
    bodyElements.length === 0 && document.body.childNodes.length == 0;
  const hasSingleH1 =
    bodyElements.length === 1 && bodyElements[0]?.tagName === "H1";
  return isEmptyBody || hasSingleH1;
}
