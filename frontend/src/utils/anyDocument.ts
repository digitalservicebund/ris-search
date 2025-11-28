import type {
  AdministrativeDirective,
  AnyDocument,
  CaseLaw,
  LegislationWork,
  Literature,
} from "~/types";

/**
 * Type guard asserting that the candidate is a caselaw document.
 *
 * @param candidate Document to check
 * @returns true if the document is a caselaw document.
 */
export function isCaselaw(candidate: AnyDocument): candidate is CaseLaw {
  return candidate["@type"] === "Decision";
}

/**
 * Type guard asserting that the candidate is a legislation work document.
 *
 * @param candidate Document to check
 * @returns true if the document is a legislation work document.
 */
export function isLegislationWork(
  candidate: AnyDocument,
): candidate is LegislationWork {
  return candidate["@type"] === "Legislation";
}

/**
 * Type guard asserting that the candidate is a literature document.
 *
 * @param candidate Document to check
 * @returns true if the document is a literature document.
 */
export function isLiterature(candidate: AnyDocument): candidate is Literature {
  return candidate["@type"] === "Literature";
}

/**
 * Type guard asserting that the candidate is an administrative directive document.
 *
 * @param candidate Document to check
 * @returns true if the document is an administrative directive document.
 */
export function isAdministrativeDirective(
  candidate: AnyDocument,
): candidate is AdministrativeDirective {
  return candidate["@type"] === "AdministrativeDirective";
}

/**
 * Returns the unique identifier of the document based on the type of the
 * document.
 *
 * @param document Document to return the identifier for
 * @returns Identifier depending on the document type
 * @throws If the document is an unsupported type or the identifier is falsy
 */
export function getIdentifier(document: AnyDocument): string {
  let id = null;

  if (isCaselaw(document) || isLiterature(document)) {
    id = document.documentNumber;
  } else if (isLegislationWork(document)) {
    id = document.workExample.legislationIdentifier;
  }

  if (!id)
    throw new Error(`Failed to identify document with ID ${document["@id"]}`);

  return id;
}
