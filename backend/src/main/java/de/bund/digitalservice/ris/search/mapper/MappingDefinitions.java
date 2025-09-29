package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.HashMap;
import java.util.Map;

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
            Map.entry(Literature.Fields.DOCUMENT_NUMBER, "documentNumber"),
            Map.entry(Literature.Fields.RECORDING_DATE, "recordingDate"),
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
            Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENT_NUMBER, "documentNumber"),
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

  public static String getOpenSearchName(String nameInSchema, ResolutionMode mode) {
    return switch (mode) {
      case ALL -> schemaToOpenSearchMap.get(nameInSchema);
      case NORMS -> normsSchemaToOpenSearchMap.get(nameInSchema);
      case CASE_LAW -> caseLawSchemaToOpenSearchMap.get(nameInSchema);
      case LITERATURE -> literatureSchemaToOpenSearchMap.get(nameInSchema);
    };
  }

  public enum ResolutionMode {
    ALL,
    NORMS,
    CASE_LAW,
    LITERATURE
  }
}
