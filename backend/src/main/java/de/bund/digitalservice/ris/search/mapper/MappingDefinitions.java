package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.HashMap;
import java.util.Map;

/**
 * The MappingDefinitions class provides static mappings between schema-specific fields and their
 * corresponding OpenSearch representation for different domains. Domains include case law, norms,
 * and literature, with the possibility of using all mappings collectively.
 *
 * <p>This class is meant to be non-instantiable and is primarily used for utility purposes. It
 * defines mappings in both directions (schema-to-OpenSearch and OpenSearch-to-schema) for various
 * fields, aiding in data conversion and resolution tasks.
 *
 * <p>Key functionalities: - Static association of schema names and OpenSearch field names across
 * multiple domains. - Utility methods to retrieve OpenSearch field names based on schema fields and
 * a specified resolution mode.
 *
 * <p>Mappings for each domain and the combined mappings are statically initialized at the class
 * level.
 *
 * <p>The class is organized around static variables categorized by domain, and these mappings are
 * populated once during initialization. This ensures the mappings remain immutable through the
 * lifetime of the application.
 */
public class MappingDefinitions {
  private MappingDefinitions() {}

  protected static final Map<String, String> caseLawOpenSearchToSchemaMap;
  protected static final Map<String, String> normsOpenSearchToSchemaMap;
  protected static final Map<String, String> literatureOpenSearchToSchemaMap;

  protected static final Map<String, String> caseLawSchemaToOpenSearchMap;

  protected static final Map<String, String> normsSchemaToOpenSearchMap;

  protected static final Map<String, String> literatureSchemaToOpenSearchMap;

  protected static final Map<String, String> schemaToOpenSearchMap;

  private static final String DATUM_FIELD = "DATUM";

  static {
    literatureOpenSearchToSchemaMap =
        Map.ofEntries(
            Map.entry(Literature.Fields.DOCUMENT_NUMBER_KEYWORD, "documentNumber"),
            Map.entry(Literature.Fields.YEARS_OF_PUBLICATION, "yearsOfPublication"),
            Map.entry(Literature.Fields.DEPENDENT_REFERENCES, "dependentReferences"),
            Map.entry(Literature.Fields.INDEPENDENT_REFERENCES, "independentReferences"),
            Map.entry(Literature.Fields.MAIN_TITLE, "mainTitle"),
            Map.entry(Literature.Fields.DOCUMENTARY_TITLE, "documentaryTitle"),
            Map.entry(Literature.Fields.AUTHORS, "authors"),
            Map.entry(Literature.Fields.COLLABORATORS, "collaborators"),
            Map.entry(Literature.Fields.SHORT_REPORT, "shortReport"),
            Map.entry(Literature.Fields.OUTLINE, "outline"),
            Map.entry(Literature.Fields.INDEXED_AT, "indexedAt"),
            Map.entry(DATUM_FIELD, "date") // alias shared with caseLaw/norms
            );
    normsOpenSearchToSchemaMap =
        Map.ofEntries(
            Map.entry(Norm.Fields.WORK_ELI_KEYWORD, "legislationIdentifier"),
            Map.entry(Norm.Fields.OFFICIAL_TITLE, "name"),
            Map.entry(Norm.Fields.OFFICIAL_SHORT_TITLE, "alternateName"),
            Map.entry(Norm.Fields.OFFICIAL_ABBREVIATION, "abbreviation"),
            Map.entry(Norm.Fields.NORMS_DATE, "legislationDate"),
            Map.entry(Norm.Fields.ENTRY_INTO_FORCE_DATE, "temporalCoverageFrom"),
            Map.entry(DATUM_FIELD, "date") // alias shared with caseLaw
            );
    caseLawOpenSearchToSchemaMap =
        Map.ofEntries(
            Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENT_NUMBER_KEYWORD, "documentNumber"),
            Map.entry(CaseLawDocumentationUnit.Fields.ECLI, "ecli"),
            Map.entry(CaseLawDocumentationUnit.Fields.CASE_FACTS, "caseFacts"),
            Map.entry(CaseLawDocumentationUnit.Fields.DECISION_GROUNDS, "decisionGrounds"),
            Map.entry(CaseLawDocumentationUnit.Fields.DISSENTING_OPINION, "dissentingOpinion"),
            Map.entry(CaseLawDocumentationUnit.Fields.GROUNDS, "grounds"),
            Map.entry(CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE, "guidingPrinciple"),
            Map.entry(CaseLawDocumentationUnit.Fields.HEADLINE, "headline"),
            Map.entry(CaseLawDocumentationUnit.Fields.HEADNOTE, "headnote"),
            Map.entry(CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE, "otherHeadnote"),
            Map.entry(CaseLawDocumentationUnit.Fields.OTHER_LONG_TEXT, "otherLongText"),
            Map.entry(CaseLawDocumentationUnit.Fields.TENOR, "tenor"),
            Map.entry(CaseLawDocumentationUnit.Fields.DECISION_DATE, "decisionDate"),
            Map.entry(CaseLawDocumentationUnit.Fields.FILE_NUMBERS, "fileNumbers"),
            Map.entry(CaseLawDocumentationUnit.Fields.COURT_TYPE, "courtType"),
            Map.entry(CaseLawDocumentationUnit.Fields.LOCATION, "location"),
            Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE, "documentType"),
            Map.entry(CaseLawDocumentationUnit.Fields.OUTLINE, "outline"),
            Map.entry(CaseLawDocumentationUnit.Fields.JUDICIAL_BODY, "judicialBody"),
            Map.entry(CaseLawDocumentationUnit.Fields.KEYWORDS, "keywords"),
            Map.entry(
                CaseLawDocumentationUnit.Fields.COURT_KEYWORD_KEYWORD, "courtName"), // differs
            Map.entry(CaseLawDocumentationUnit.Fields.DECISION_NAME, "decisionName"),
            Map.entry(
                CaseLawDocumentationUnit.Fields.DEVIATING_DOCUMENT_NUMBER,
                "deviatingDocumentNumber"),
            Map.entry(CaseLawDocumentationUnit.Fields.PUBLICATION_STATUS, "publicationStatus"),
            Map.entry(CaseLawDocumentationUnit.Fields.ERROR, "error"),
            Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENTATION_OFFICE, "documentationOffice"),
            Map.entry(CaseLawDocumentationUnit.Fields.PROCEDURES, "procedures"),
            Map.entry(CaseLawDocumentationUnit.Fields.LEGAL_EFFECT, "legalEffect"),
            Map.entry(CaseLawDocumentationUnit.Fields.INDEXED_AT, "indexedAt"),
            Map.entry(DATUM_FIELD, "date") // alias shared with norms
            );
    schemaToOpenSearchMap = new HashMap<>();

    caseLawSchemaToOpenSearchMap = new HashMap<>();
    caseLawOpenSearchToSchemaMap.forEach(
        (key, value) -> caseLawSchemaToOpenSearchMap.put(value, key));

    normsSchemaToOpenSearchMap = new HashMap<>();
    normsOpenSearchToSchemaMap.forEach((key, value) -> normsSchemaToOpenSearchMap.put(value, key));

    literatureSchemaToOpenSearchMap = new HashMap<>();
    literatureOpenSearchToSchemaMap.forEach(
        (key, value) -> literatureSchemaToOpenSearchMap.put(value, key));

    schemaToOpenSearchMap.putAll(caseLawSchemaToOpenSearchMap);
    schemaToOpenSearchMap.putAll(normsSchemaToOpenSearchMap);
    schemaToOpenSearchMap.putAll(literatureSchemaToOpenSearchMap);
  }

  /**
   * Maps a schema-specific name to its corresponding OpenSearch name based on the provided
   * resolution mode.
   *
   * @param nameInSchema The name in the schema to be resolved to an OpenSearch name. Must not be
   *     null.
   * @param mode The resolution mode indicating the specific schema mapping to use. Must not be
   *     null.
   * @return The corresponding OpenSearch name for the given schema-specific name, or null if no
   *     mapping exists.
   */
  public static String getOpenSearchName(String nameInSchema, ResolutionMode mode) {
    return switch (mode) {
      case ALL -> schemaToOpenSearchMap.get(nameInSchema);
      case NORMS -> normsSchemaToOpenSearchMap.get(nameInSchema);
      case CASE_LAW -> caseLawSchemaToOpenSearchMap.get(nameInSchema);
      case LITERATURE -> literatureSchemaToOpenSearchMap.get(nameInSchema);
    };
  }

  /**
   * Enumeration representing different resolution modes for mapping definitions.
   *
   * <p>The ResolutionMode determines which specific mapping schema is used when retrieving or
   * converting data between schemas and OpenSearch indices. Each enum constant corresponds to a
   * distinct domain within the mapping definitions.
   *
   * <p>- ALL: Indicates that all mapping schemas are considered. - NORMS: Specific to norms-related
   * schema mapping. - CASE_LAW: Specific to case law-related schema mapping. - LITERATURE: Specific
   * to literature-related schema mapping.
   *
   * <p>The ResolutionMode is typically used in conjunction with mapping methods to specify the
   * context or domain of the mapping operation.
   */
  public enum ResolutionMode {
    ALL,
    NORMS,
    CASE_LAW,
    LITERATURE
  }
}
