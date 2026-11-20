// Convention:
//
// - `take...` removes content from the document, mutating it in the process
// - `get...` only reads content and doesn't change it

/**
 * Removes all elements matching the selector and returns their HTML,
 * concatenated in document order.
 *
 * Mutates the input document.
 *
 * @param options.innerHtml Return only what is inside the elements, without the
 *   elements themselves
 * @returns The HTML of the removed elements, or undefined if nothing matched.
 */
export function takeElements(
  document: Document,
  selector: string,
  options: { innerHtml?: boolean } = {},
): string | undefined {
  const matches = [...document.querySelectorAll(selector)];

  // A match inside another match is already part of the outer one
  const outermost: Element[] = [];
  for (const element of matches) {
    if (!outermost.at(-1)?.contains(element)) outermost.push(element);
  }
  if (outermost.length === 0) return undefined;

  const html = outermost
    .map((element) =>
      options.innerHtml ? element.innerHTML : element.outerHTML,
    )
    .join("");
  for (const element of outermost) element.remove();
  return html;
}

/**
 * Reads the text content of all elements matching the selector, in document
 * order. The text is trimmed, elements without text are skipped.
 *
 * @param root The document, or an element to search in
 * @returns One string per matching element, empty if nothing matched.
 */
export function getText(root: ParentNode, selector: string): string[] {
  return [...root.querySelectorAll(selector)]
    .map((element) => element.textContent?.trim())
    .filter((text): text is string => Boolean(text));
}

export type Section = { id: string; title: string };

/**
 * Lists the sections of a document, e.g. for a table of contents. The title of
 * a section is the text of its first `<h2>` that has text.
 *
 * @param elementName The tag name of the sections (e.g., "section").
 */
export function getSections(
  document: Document,
  elementName: string,
): Section[] {
  return [...document.querySelectorAll(elementName)].map((element) => ({
    id: element.id,
    title: getText(element, "h2")[0] ?? "",
  }));
}
