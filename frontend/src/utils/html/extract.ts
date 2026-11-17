// Convention: helpers that remove content from the document are called
// `take*` and say that they mutate it. Helpers that only read are called
// `get*` and never change the document.

/**
 * **Mutates the document:** removes all elements matching the selector and
 * returns their HTML, concatenated in document order. Use this in an extraction
 * to move a part out of the body.
 *
 * @returns The HTML of the removed elements, or undefined if nothing matched.
 */
export function takeElements(
  document: Document,
  selector: string,
): string | undefined {
  const matches = [...document.querySelectorAll(selector)];

  // A match inside another match is already part of the outer one.
  const outermost = matches.filter(
    (element) =>
      !matches.some((other) => other !== element && other.contains(element)),
  );
  if (outermost.length === 0) return undefined;

  const html = outermost.map((element) => element.outerHTML).join("");
  for (const element of outermost) element.remove();
  return html;
}

