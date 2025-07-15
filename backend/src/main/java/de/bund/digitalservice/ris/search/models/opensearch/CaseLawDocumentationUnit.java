package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.opensearch.common.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Model class representing a case law to opensearch index. This class is annotated with Lombok
 * annotations for generating getters, setters, constructors, and builder methods.
 */
@Builder
@Document(indexName = "#{@configurations.getCaseLawsIndexName()}")
@Setting(settingPath = "/openSearch/german_analyzer.json")
@Mapping(mappingPath = "/openSearch/caselaw_mappings.json")
public record CaseLawDocumentationUnit(
    @JsonIgnore @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Field(name = Fields.ECLI) String ecli,
    @Field(name = Fields.CASE_FACTS) String caseFacts,
    @Field(name = Fields.DECISION_GROUNDS) String decisionGrounds,
    @Field(name = Fields.DISSENTING_OPINION) String dissentingOpinion,
    @Field(name = Fields.GROUNDS) String grounds,
    @Field(name = Fields.GUIDING_PRINCIPLE) String guidingPrinciple,
    @Field(name = Fields.HEADLINE) String headline,
    @Field(name = Fields.HEADNOTE) String headnote,
    @Field(name = Fields.OTHER_HEADNOTE) String otherHeadnote,
    @Field(name = Fields.OTHER_LONG_TEXT) String otherLongText,
    @Field(name = Fields.TENOR, type = FieldType.Text) String tenor,
    @Field(name = Fields.DECISION_DATE, type = FieldType.Date, format = DateFormat.date)
        LocalDate decisionDate,
    @ElementCollection @Field(name = Fields.FILE_NUMBERS) List<String> fileNumbers,
    @Field(name = Fields.COURT_TYPE) String courtType,
    @Field(name = Fields.LOCATION) String location,
    @Field(name = Fields.DOCUMENT_TYPE) String documentType,
    @Field(name = Fields.OUTLINE) String outline,
    @Field(name = Fields.JUDICIAL_BODY) String judicialBody,
    @ElementCollection @Field(name = Fields.KEYWORDS) List<String> keywords,
    @Field(name = Fields.COURT_KEYWORD) String courtKeyword,
    @ElementCollection @Field(name = Fields.DECISION_NAME) List<String> decisionName,
    @ElementCollection @Field(name = Fields.DEVIATING_DOCUMENT_NUMBER)
        List<String> deviatingDocumentNumber,
    @JsonIgnore @Field(name = Fields.PUBLICATION_STATUS) String publicationStatus,
    @JsonIgnore @Field(type = FieldType.Boolean, name = Fields.ERROR) Boolean error,
    @JsonIgnore @Field(name = Fields.DOCUMENTATION_OFFICE) String documentationOffice,
    @JsonIgnore @ElementCollection @Field(name = Fields.PROCEDURES) List<String> procedures,
    @JsonIgnore @Field(name = Fields.LEGAL_EFFECT) String legalEffect,
    @JsonIgnore @Field(name = Fields.INDEXED_AT) String indexedAt,
    @Nullable @Field(name = Fields.ARTICLES) List<Article> articles)
    implements AbstractSearchEntity {
  public static class Fields {
    private Fields() {}

    /** Field holding the court type and court location */
    public static final String COURT_KEYWORD = "court_keyword";

    public static final String COURT_KEYWORD_KEYWORD = "court_keyword.keyword";
    public static final String ID = "id";
    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String ECLI = "ecli";
    public static final String ECLI_KEYWORD = "ecli.keyword";
    public static final String CASE_FACTS = "case_facts";
    public static final String DECISION_GROUNDS = "decision_grounds";
    public static final String DISSENTING_OPINION = "dissenting_opinion";
    public static final String GROUNDS = "grounds";
    public static final String GUIDING_PRINCIPLE = "guiding_principle";
    public static final String HEADLINE = "headline";
    public static final String HEADNOTE = "headnote";
    public static final String OTHER_HEADNOTE = "other_headnote";
    public static final String OTHER_LONG_TEXT = "other_long_text";
    public static final String TENOR = "tenor";
    public static final String DECISION_DATE = "decision_date";
    public static final String FILE_NUMBERS = "file_numbers";
    public static final String FILE_NUMBERS_KEYWORD = "file_numbers.keyword";

    /** Field holding the type of court, e.g., FG, BVerwG */
    public static final String COURT_TYPE = "court_type";

    public static final String LOCATION = "location";
    public static final String DOCUMENT_TYPE = "document_type";
    public static final String OUTLINE = "outline";
    public static final String JUDICIAL_BODY = "judicial_body";
    public static final String KEYWORDS = "keywords";
    public static final String DECISION_NAME = "decision_name";
    public static final String DEVIATING_DOCUMENT_NUMBER = "deviating_document_number";
    public static final String PUBLICATION_STATUS = "publication_status";
    public static final String ERROR = "error";
    public static final String DOCUMENTATION_OFFICE = "documentation_office";
    public static final String PROCEDURES = "procedures";
    public static final String LEGAL_EFFECT = "legal_effect";
    public static final String INDEXED_AT = "indexed_at";
    public static final String ARTICLES = "articles";
  }
}
