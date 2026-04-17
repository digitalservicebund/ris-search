import type { components, operations } from "./api-generated.d.ts";
export type { components, operations } from "./api-generated.d.ts";

// Shared
export type LegalForceStatus = NonNullable<
  components["schemas"]["LegislationExpressionSchema"]["legislationLegalForce"]
>;

export type TextMatch = components["schemas"]["TextMatchSchema"];

export interface SearchResult<T> {
  item: T;
  textMatches: TextMatch[];
}

export interface JSONLDList<T> {
  "@type"?: string;
  "@id"?: string;
  totalItems?: number;
  member?: T[];
  view?: components["schemas"]["PartialCollectionViewSchema"];
}

// Statistics
export type Statistics = components["schemas"]["StatisticsApiSchema"];

// Case law
export type CaseLaw = components["schemas"]["CaseLawSchema"];
export type CaseLawEncoding = components["schemas"]["CaseLawEncodingSchema"];

// Legislation
export type LegislationWork = components["schemas"]["LegislationWorkSchema"];
export type PublicationSchema = components["schemas"]["PublicationIssueSchema"];

export type LegislationManifestation =
  components["schemas"]["LegislationObjectSchema"];

export type LegislationExpression =
  components["schemas"]["LegislationExpressionSchema"];

export type LegislationExpressionPartSchema =
  components["schemas"]["LegislationExpressionPartSchema"];

export type Article = components["schemas"]["LegislationExpressionPartSchema"];

// Literature
export type Literature = components["schemas"]["LiteratureSchema"];
export type LiteratureEncoding =
  components["schemas"]["LiteratureEncodingSchema"];

// Administrative directives
export type AdministrativeDirective =
  components["schemas"]["AdministrativeDirectiveSchema"];
export type AdministrativeDirectiveEncoding =
  components["schemas"]["AdministrativeDirectiveEncodingSchema"];

// Search / court
export type CourtSearchResult = components["schemas"]["CourtSearchResult"];

// Query parameter types for API endpoints
type QueryParams<T extends keyof operations> = NonNullable<
  operations[T]["parameters"]["query"]
>;

export type DocumentSearchParams = QueryParams<"searchAndFilter_2">;
export type LuceneSearchParams = QueryParams<"search">;
export type LegislationSearchParams = QueryParams<"searchAndFilter_1">;
export type CourtsSearchParams = QueryParams<"getCourts">;

// Frontend-only types
export type AnyDocument =
  | CaseLaw
  | LegislationExpression
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
