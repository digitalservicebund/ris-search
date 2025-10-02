import { DocumentKind } from "~/types";

/**
 * Returns true if a string corresponds to a document kind.
 *
 * @param maybe String to check
 * @returns Whether the string matches a document kind
 */
export function isDocumentKind(maybe: string): maybe is DocumentKind {
  return Object.values(DocumentKind).includes(maybe as DocumentKind);
}
