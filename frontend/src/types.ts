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
  inLanguage: string | null;
  documentNumber: string | null;
  yearsOfPublication: string[];
  documentTypes: string[];
  dependentReferences: string[];
  independentReferences: string[];
  normReferences: string[];
  headline: string | null;
  alternativeHeadline: string | null;
  headlineAdditions: string | null;
  authors: string[];
  collaborators: string[];
  originators: string[];
  conferenceNotes: string[];
  universityNotes: string[];
  languages: string[];
  shortReport: string | null;
  outline: string | null;
  editors: string[];
  founder: string[];
  publishers: string[];
  publishingHouses: string[];
  edition: string | null;
  internationalIdentifiers: string[];
  volumes: string[];
  literatureType: "sli" | "uli";
  encoding: LiteratureEncoding[] | null;
}

export interface LiteratureEncoding {
  "@type": "MediaObject";
  "@id": string;
  contentUrl: string | null;
  encodingFormat: string | null;
  inLanguage: string | null;
}

export interface AdministrativeDirective {
  "@type": "AdministrativeDirective";
  "@id": string;
  documentNumber: string;
  headline?: string;
  shortReport?: string;
  documentType: string;
  documentTypeDetail?: string;
  referenceNumbers: string[];
  entryIntoForceDate?: string;
  expiryDate?: string;
  legislationAuthority?: string;
  references: string[];
  citationDates: string[];
  normReferences: string[];
  outline: string[];
  encoding?: AdministrativeDirectiveEncoding[];
}

export interface AdministrativeDirectiveEncoding {
  "@type": "MediaObject";
  "@id": string;
  contentUrl: string | null;
  encodingFormat: string | null;
  inLanguage: string | null;
}

export type AnyDocument =
  | CaseLaw
  | LegislationWork
  | Literature
  | AdministrativeDirective;

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
  /**
   * Verwaltungsvorschriften
   */
  AdministrativeDirective = "V",

  All = "A",
}

export type Statistics = {
  [K in
    | "case-law"
    | "legislation"
    | "literature"
    | "administrative-directive"]: { count: number };
};

export type CourtSearchResult = {
  id: string;
  label: string;
  count?: number;
};
