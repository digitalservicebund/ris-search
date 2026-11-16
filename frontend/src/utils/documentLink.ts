import type { RouteLocationRaw } from "#vue-router";

export type DocumentLink = {
  text: string;
  to?: RouteLocationRaw;
};

/**
 * Builds a link to the detail page of a document.
 *
 * @param text Link text
 * @param basePath Path of the document kind, e.g. "/gerichtsentscheidungen"
 * @param dokumentnummer Document number. Without it, the link has no target
 * @param from Page the user comes from, passed on as query parameter
 * @returns The link, or undefined if there is no text
 */
export function toDocumentLink(
  text: string | undefined,
  basePath: string,
  dokumentnummer?: string,
  from?: string,
): DocumentLink | undefined {
  if (!text) return undefined;
  if (!dokumentnummer) return { text };

  return {
    text,
    to: { path: `${basePath}/${dokumentnummer}`, query: { from } },
  };
}
