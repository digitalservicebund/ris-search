package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
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
    @Getter @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Field(name = Fields.DOCUMENT_NUMBER_KEYWORD) String documentNumberKeyword,
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
    @Nullable @Field(name = Fields.EDITION) String edition,
    @ElementCollection @Field(name = Fields.AUTHORS) List<String> authors,
    @ElementCollection @Field(name = Fields.COLLABORATORS) List<String> collaborators,
    @ElementCollection @Field(name = Fields.LANGUAGE) List<String> languages,
    @ElementCollection @Field(name = Fields.ORIGINATOR) List<String> originators,
    @ElementCollection @Field(name = Fields.CONFERENCE_NOTE) List<String> conferenceNotes,
    @ElementCollection @Field(name = Fields.UNIVERSITY_NOTES) List<String> universityNotes,
    @ElementCollection @Field(name = Fields.EDITOR) List<String> editors,
    @ElementCollection @Field(name = Fields.FOUNDER) List<String> founder,
    @ElementCollection @Field(name = Fields.PUBLISHER_ORGANIZATIONS)
        List<String> publisherOrganizations,
    @ElementCollection @Field(name = Fields.PUBLISHER_PERSONS) List<String> publisherPersons,
    @ElementCollection @Field(name = Fields.SHORT_TITLES) List<String> shortTitles,
    @ElementCollection @Field(name = Fields.ADDITIONAL_TITLES) List<String> additionalTitles,
    @ElementCollection @Field(name = Fields.FULL_TITLE_ADDITIONS) List<String> fullTitleAdditions,
    @ElementCollection @Field(name = Fields.FOOTNOTES) List<String> footnotes,
    @ElementCollection @Field(name = Fields.PUBLISHER_INFORMATION)
        List<String> publisherInformation,
    @ElementCollection @Field(name = Fields.INTERNATIONAL_IDENTIFIERS)
        List<String> internationalIdentifiers,
    @ElementCollection @Field(name = Fields.VOLUMES) List<String> volumes,
    @Nullable @Field(name = Fields.SHORT_REPORT) String shortReport,
    @Nullable @Field(name = Fields.OUTLINE) String outline,
    @JsonIgnore @Field(name = Fields.INDEXED_AT) String indexedAt)
    implements AbstractSearchEntity {

  /**
   * Class containing constant field names used for mapping properties within the associated
   * Literature class. These constants represent the keys for data retrieval and mapping from a data
   * source. This class is not meant to be instantiated.
   */
  public static class Fields {
    private Fields() {}

    /** sonstige sachtitle */
    public static final String ADDITIONAL_TITLES = "additional_titles";

    /** Verfasser * */
    public static final String AUTHORS = "authors";

    /** Mitarbeiter * */
    public static final String COLLABORATORS = "collaborators";

    /** Kongressvermerk * */
    public static final String CONFERENCE_NOTE = "conference_note";

    /** unselbstständige Fundstellen * */
    public static final String DEPENDENT_REFERENCES = "dependent_references";

    /** Dokumentarischer Titel * */
    public static final String DOCUMENTARY_TITLE = "documentary_title";

    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String DOCUMENT_NUMBER_KEYWORD = "document_number.keyword";
    public static final String DOCUMENT_TYPES = "document_types";

    /** Ausgabe */
    public static final String EDITION = "edition";

    /** Bearbeiter */
    public static final String EDITOR = "editor";

    public static final String FIRST_PUBLICATION_DATE = "first_publication_date";

    /** Fußnoten */
    public static final String FOOTNOTES = "footnotes";

    /** Begruender */
    public static final String FOUNDER = "founder";

    /** gesamttitel angaben */
    public static final String FULL_TITLE_ADDITIONS = "full_title_additions";

    public static final String ID = "id";

    /** selbständige Fundstellen */
    public static final String INDEPENDENT_REFERENCES = "independent_references";

    /** Used internally to store at what time the document was indexed */
    public static final String INDEXED_AT = "indexed_at";

    /** Internationale Standardnummern */
    public static final String INTERNATIONAL_IDENTIFIERS = "international_identifiers";

    /** Sprache * */
    public static final String LANGUAGE = "language";

    /** Haupttitel* */
    public static final String MAIN_TITLE = "main_title";

    /** Zusätze zum Hauptsachtitel * */
    public static final String MAIN_TITLE_ADDITIONS = "main_title_additions";

    /** Norm Verweise* */
    public static final String NORM_REFERENCES = "norm_references";

    /** Urheber * */
    public static final String ORIGINATOR = "originator";

    /** Gliederung * */
    public static final String OUTLINE = "outline";

    /** Verlagsangaben */
    public static final String PUBLISHER_INFORMATION = "publisher_information";

    /** Herausgeber (Institution) */
    public static final String PUBLISHER_ORGANIZATIONS = "publisher_organizations";

    /** Herausgeber (natürliche Person) */
    public static final String PUBLISHER_PERSONS = "publisher_persons";

    /** Kurzrefarat * */
    public static final String SHORT_REPORT = "short_report";

    /** titelkurzformen */
    public static final String SHORT_TITLES = "short_titles";

    /** Hochschulvermerk */
    public static final String UNIVERSITY_NOTES = "university_note";

    /** Teilbaende */
    public static final String VOLUMES = "volumes";

    public static final String YEARS_OF_PUBLICATION = "years_of_publication";
  }
}
