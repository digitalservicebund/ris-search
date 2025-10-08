export interface JSONLDList<T> {
  "@type": "hydra:Collection";
  totalItems: number;
  member: T[];
  view: {
    previous: string | null;
    next: string | null;
    first: string;
    last: string;
  };
}

export interface TextMatch {
  "@type": "SearchResultMatch";
  name: string;
  text: string;
  location: string | null;
}

export interface SearchResult<T> {
  item: T;
  textMatches: TextMatch[];
}

export interface LegislationWork {
  "@type": "Legislation";
  "@id": string;
  name: string;
  legislationIdentifier: string;
  alternateName: string;
  abbreviation?: string;
  legislationDate: string;
  datePublished: string;
  isPartOf: PublicationSchema;
  workExample: LegislationExpression;
}

export type LegalForceStatus = "InForce" | "NotInForce" | "PartiallyInForce";
export interface LegislationExpression {
  "@type": "Legislation";
  "@id": string;
  legislationIdentifier: string;
  legislationLegalForce: LegalForceStatus;
  temporalCoverage: string;
  encoding: LegislationManifestation[];
  tableOfContents: TableOfContentsItem[];
  hasPart: Article[];
}

export interface PublicationSchema {
  name: string;
}

export interface LegislationManifestation {
  "@type": "LegislationObject";
  "@id": string;
  contentUrl: string;
  encodingFormat: string;
  inLanguage: string;
}

export interface TableOfContentsItem {
  "@type": "TocEntry";
  id: string;
  marker: string;
  heading: string;
  children: TableOfContentsItem[];
}

export interface Article {
  "@type": "Legislation";
  "@id": string;
  eId: string;
  guid: string | null;
  name: string;
  isActive: boolean | null;
  entryIntoForceDate: string | null;
  expiryDate: string | null;
  encoding: LegislationManifestation[] | null;
}

export interface CaseLaw {
  "@type": "Decision";
  "@id": string;
  documentNumber: string;
  ecli: string;
  caseFacts?: string;
  decisionGrounds?: string;
  dissentingOpinion?: string;
  grounds?: string;
  guidingPrinciple?: string;
  headline?: string;
  headnote?: string;
  otherHeadnote?: string;
  otherLongText?: string;
  tenor?: string;
  decisionDate: string;
  fileNumbers: string[];
  courtType?: string;
  location?: string;
  documentType?: string;
  outline?: string;
  judicialBody?: string;
  keywords: string[];
  courtName?: string;
  decisionName: string[];
  deviatingDocumentNumber: string[];
  inLanguage: string;
  encoding: CaseLawEncoding[];
}

export interface CaseLawEncoding {
  "@type": "DecisionObject";
  "@id": string;
  contentUrl: string;
  encodingFormat: string;
  inLanguage: string;
}

export interface Literature {
  "@type": "Literature";
  "@id": string;
  inLanguage: string;
  documentNumber: string;
  recordingDate: string; // LocalDate in backend -> string in ISO format
  yearsOfPublication: string[];
  documentTypes: string[];
  dependentReferences: string[];
  independentReferences: string[];
  headline?: string;
  alternativeTitle?: string;
  authors?: string[];
  collaborators?: string[];
  shortReport?: string;
  outline?: string;
  encoding: LiteratureEncoding[];
}

export interface LiteratureEncoding {
  "@type": "LiteratureObject";
  "@id": string;
  contentUrl: string;
  encodingFormat: string;
  inLanguage: string;
}

export type AnyDocument = CaseLaw | LegislationWork | Literature;

export enum DocumentKind {
  /**
   * Rechtsprechung
   */
  CaseLaw = "R",
  /**
   * Rechtsnorm: Gesetze, Satzungen und Rechtsverordnungen
   */
  Norm = "N",
  /**
   * Literatur
   */
  Literature = "L",

  All = "A",
}

export function shortDocumentType(
  kind: "Legislation" | "Decision" | "Literature",
) {
  switch (kind) {
    case "Decision":
      return "R"; // Rechtsprechung
    case "Legislation":
      return "N"; // Norm
    case "Literature":
      return "L"; // Literatur
    default:
      return "-";
  }
}
