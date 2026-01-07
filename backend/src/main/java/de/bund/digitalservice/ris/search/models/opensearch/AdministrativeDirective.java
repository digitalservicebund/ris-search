package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.Nullable;

/**
 * Represents an administrative directive within the system. This record is modeled to handle
 * various metadata and details associated with administrative directives. It implements the {@code
 * AbstractSearchEntity} interface to ensure compatibility with the search infrastructure.
 *
 * <p>Each administrative directive may consist of information such as document numbers, type,
 * content, legislation authority, effective dates, referenced norms and caselaws, keywords, and
 * associated legal fields.
 *
 * <p>Annotated with Elasticsearch indexing metadata, this record is designed for persistence and
 * efficient querying in a search index.
 *
 * <p>Fields include: - Identification (ID and document number). - Metadata for classification and
 * type details. - Content summaries and headline. - References to norms, caselaws, and related
 * documents. - Effective and expiry dates for the directive. - Keywords and legal classifications
 * associated with the directive. - Additional utility fields for indexing.
 */
@Builder
@Document(indexName = "#{@configurations.getAdministrativeDirectiveIndexName()}")
public record AdministrativeDirective(
    @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Nullable @Field(name = Fields.HEADLINE) String headline,
    @Field(name = Fields.DOCUMENT_TYPE) String documentType,
    @Nullable @Field(name = Fields.DOCUMENT_TYPE_DETAIL) String documentTypeDetail,
    @Nullable @Field(name = Fields.SHORT_REPORT) String shortReport,
    @Nullable @Field(name = Fields.LEGISLATION_AUTHORITY) String legislationAuthority,
    @Nullable
        @Field(
            name = Fields.ENTRY_INTO_EFFECT_DATE,
            type = FieldType.Date,
            format = DateFormat.date)
        LocalDate entryIntoEffectDate,
    @Nullable @Field(name = Fields.EXPIRY_DATE, type = FieldType.Date, format = DateFormat.date)
        LocalDate expiryDate,
    @ElementCollection @Field(name = Fields.NORM_REFERENCES) List<String> normReferences,
    @ElementCollection @Field(name = Fields.CASELAW_REFERENCES) List<String> caselawReferences,
    @ElementCollection @Field(name = Fields.REFERENCES) List<String> references,
    @ElementCollection
        @Field(name = Fields.CITATION_DATES, type = FieldType.Date, format = DateFormat.date)
        List<LocalDate> citationDates,
    @ElementCollection @Field(name = Fields.REFERENCE_NUMBERS) List<String> referenceNumbers,
    @ElementCollection @Field(name = Fields.ACTIVE_ADMINISTRATIVE_REFERENCES)
        List<String> activeAdministrativeReferences,
    @ElementCollection @Field(name = Fields.ACTIVE_NORM_REFERENCES)
        List<String> activeNormReferences,
    @ElementCollection @Field(name = Fields.KEYWORDS) List<String> keywords,
    @ElementCollection @Field(name = Fields.FIELDS_OF_LAW) List<String> fieldsOfLaw,
    @ElementCollection @Field(name = Fields.OUTLINE) List<String> tableOfContentsEntries,
    @JsonIgnore @Field(name = AdministrativeDirective.Fields.INDEXED_AT) String indexedAt)
    implements AbstractSearchEntity {

  /**
   * Defines a collection of static constant field names used as keys for referencing and mapping
   * specific properties of an administrative directive entity in a structured manner.
   */
  public static class Fields {
    private Fields() {}

    public static final String ID = "id";

    public static final String DOCUMENT_NUMBER = "document_number";

    public static final String HEADLINE = "headline";

    public static final String DOCUMENT_TYPE_DETAIL = "document_type_detail";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String SHORT_REPORT = "short_report";

    public static final String LEGISLATION_AUTHORITY = "legislation_authority";

    public static final String ENTRY_INTO_EFFECT_DATE = "entry_into_effect_date";

    public static final String EXPIRY_DATE = "expiry_date";

    public static final String NORM_REFERENCES = "norm_references";

    public static final String CASELAW_REFERENCES = "caselaw_references";

    public static final String REFERENCES = "references";

    public static final String CITATION_DATES = "citation_dates";

    public static final String REFERENCE_NUMBERS = "reference_numbers";

    public static final String ACTIVE_ADMINISTRATIVE_REFERENCES =
        "active_administrative_references";

    public static final String ACTIVE_NORM_REFERENCES = "active_norm_references";

    public static final String KEYWORDS = "keywords";

    public static final String FIELDS_OF_LAW = "fields_of_law";

    public static final String OUTLINE = "outline";

    public static final String INDEXED_AT = "indexed_at";
  }
}
