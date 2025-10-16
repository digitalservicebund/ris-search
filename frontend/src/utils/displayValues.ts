import { DocumentKind } from "~/types";

const documentKindDisplayValues: Record<DocumentKind, string> = {
  [DocumentKind.CaseLaw]: "Gerichtsentscheidungen",
  [DocumentKind.Norm]: "Gesetze & Verordnungen",
  [DocumentKind.Literature]: "Literaturnachweise",
  [DocumentKind.All]: "Datensätze",
};

/**
 * Returns a human readable label for the specified document kind.
 *
 * @param documentKind Document kind to be displayed
 * @returns Display value
 */
export function formatDocumentKind(documentKind: DocumentKind): string {
  return documentKindDisplayValues[documentKind] ?? documentKindDisplayValues.A;
}
