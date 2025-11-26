import { DocumentKind } from "~/types";

/**
 * Queryable fields that can be added to the search query for advanced searches.
 */
export type DataField = {
  /** Human-readable label for the user */
  label: string;

  /**
   * The pattern to be inserted when the field is added by the user. The pattern
   * can include a $ sign to indicate where the cursor should be positioned when
   * the pattern is inserted. The $ sign will be removed from the string. If no
   * $ is included, the cursor will be positioned at the end. If more than one
   * is included, only the first is considered.
   */
  pattern: string;
};

/**
 * Query fields that should be displayed for each document kind.
 */
export const queryableDataFields: Record<DocumentKind, DataField[]> = {
  [DocumentKind.All]: [],
  [DocumentKind.Norm]: [
    { label: "Überschriften der Paragraphen", pattern: "UP:" },
    { label: "Text der Paragraphen", pattern: "TP:" },
    { label: "Kurzüberschrift ", pattern: "KU:" },
    { label: "Langüberschrift", pattern: "LU:" },
    { label: "Buchstabenabkürzung", pattern: "AB:" },
  ],
  [DocumentKind.CaseLaw]: [
    { label: "Aktenzeichen", pattern: "AZ:" },
    { label: "Leitsatz", pattern: "LS:" },
    { label: "Gericht", pattern: "G:" },
    { label: "Gründe", pattern: "GR:" },
    { label: "Tatbestand", pattern: "TB:" },
    { label: "Tenor", pattern: "TN:" },
    { label: "Orientierungssatz", pattern: "OS:" },
    { label: "Dokumenttyp", pattern: "DT:" },
    { label: "Entscheidungsgründe", pattern: "EGR:" },
    { label: "ECLI", pattern: "ECLI:" },
  ],
  [DocumentKind.Literature]: [
    { label: "Titel", pattern: "T:" },
    { label: "Verfasser", pattern: "V:" },
    { label: "Fundstelle", pattern: "FU:" },
    { label: "Norm", pattern: "N:" },
  ],
  [DocumentKind.AdministrativeDirective]: [],
};
