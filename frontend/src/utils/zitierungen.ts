import sanitizeHtml from "sanitize-html";

export type Zitierungen = {
  aktivRechtsprechung?: string;
  passivRechtsprechung?: string;
  passivLiteratur?: string;
};

function getTemplate(
  document: Document,
  templateId: string,
): HTMLTemplateElement | null {
  return document.head.querySelector<HTMLTemplateElement>(
    `template#${templateId}`,
  );
}

function getMergedListHtml(
  document: Document,
  templateIds: string[],
): string | undefined {
  const items = templateIds.flatMap((templateId) =>
    Array.from(
      getTemplate(document, templateId)?.content.querySelectorAll("li") ?? [],
      (li) => li.outerHTML,
    ),
  );
  return items.length > 0 ? `<ul>${items.join("")}</ul>` : undefined;
}

function sanitize(html?: string): string | undefined {
  return (
    (html && sanitizeHtml(html, { allowedTags: ["ul", "li", "a"] })) ||
    undefined
  );
}

export function getZitierungen(document: Document): Zitierungen | undefined {
  const zitierungen: Zitierungen = {
    aktivRechtsprechung: sanitize(
      getTemplate(document, "referenzRechtsprechungAktiv")?.innerHTML,
    ),
    passivRechtsprechung: sanitize(
      getTemplate(document, "referenzRechtsprechungPassiv")?.innerHTML,
    ),
    passivLiteratur: sanitize(
      getMergedListHtml(document, [
        "referenzSelbststaendigeLiteraturPassiv",
        "referenzUnselbststaendigeLiteraturPassiv",
      ]),
    ),
  };
  return Object.values(zitierungen).some(Boolean) ? zitierungen : undefined;
}
