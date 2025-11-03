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

/** Model class representing a literature opensearch index. */
@Builder
@Document(indexName = "#{@configurations.getLiteratureIndexName()}")
public record Literature(
    @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @ElementCollection @Field(name = Fields.YEARS_OF_PUBLICATION) List<String> yearsOfPublication,
    @Field(name = Fields.FIRST_PUBLICATION_DATE, type = FieldType.Date, format = DateFormat.date)
        LocalDate firstPublicationDate,
    @ElementCollection @Field(name = Fields.DOCUMENT_TYPES) List<String> documentTypes,
    @ElementCollection @Field(name = Fields.DEPENDENT_REFERENCES) List<String> dependentReferences,
    @ElementCollection @Field(name = Fields.INDEPENDENT_REFERENCES)
        List<String> independentReferences,
    @ElementCollection @Field(name = Fields.NORM_REFERENCES) List<String> normReferences,
    @Nullable @Field(name = Fields.MAIN_TITLE) String mainTitle,
    @Nullable @Field(name = Fields.DOCUMENTARY_TITLE) String documentaryTitle,
    @Nullable @Field(name = Fields.MAIN_TITLE_ADDITIONS) String mainTitleAdditions,
    @ElementCollection @Field(name = Fields.AUTHORS) List<String> authors,
    @ElementCollection @Field(name = Fields.COLLABORATORS) List<String> collaborators,
    @ElementCollection @Field(name = Fields.LANGUAGE) List<String> languages,
    @ElementCollection @Field(name = Fields.ORIGINATOR) List<String> originators,
    @ElementCollection @Field(name = Fields.CONFERENCE_NOTE) List<String> conferenceNotes,
    @Nullable @Field(name = Fields.SHORT_REPORT) String shortReport,
    @Nullable @Field(name = Fields.OUTLINE) String outline,
    @JsonIgnore @Field(name = Fields.INDEXED_AT) String indexedAt)
    implements AbstractSearchEntity {
  public static class Fields {
    private Fields() {}

    public static final String ID = "id";
    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String DOCUMENT_NUMBER_KEYWORD = "document_number.keyword";
    public static final String YEARS_OF_PUBLICATION = "years_of_publication";
    public static final String FIRST_PUBLICATION_DATE = "first_publication_date";
    public static final String DOCUMENT_TYPES = "document_types";

    /** unselbstständige Fundstellen * */
    public static final String DEPENDENT_REFERENCES = "dependent_references";

    /** selbständige Fundstellen * */
    public static final String INDEPENDENT_REFERENCES = "independent_references";

    /** Norm Verweise* */
    public static final String NORM_REFERENCES = "norm_references";

    /** Haupttitel* */
    public static final String MAIN_TITLE = "main_title";

    /** Zusätze zum Hauptsachtitel * */
    public static final String MAIN_TITLE_ADDITIONS = "main_title_additions";

    /** Dokumentarischer Titel * */
    public static final String DOCUMENTARY_TITLE = "documentary_title";

    /** Verfasser * */
    public static final String AUTHORS = "authors";

    /** Mitarbeiter * */
    public static final String COLLABORATORS = "collaborators";

    /** Kurzrefarat * */
    public static final String SHORT_REPORT = "short_report";

    /** Gliederung * */
    public static final String OUTLINE = "outline";

    /** Urheber * */
    public static final String ORIGINATOR = "originator";

    /** Sprache * */
    public static final String LANGUAGE = "language";

    /** Kongressvermerk * */
    public static final String CONFERENCE_NOTE = "conference_note";

    /** Used internally to store at what time the document was indexed * */
    public static final String INDEXED_AT = "indexed_at";
  }
}
