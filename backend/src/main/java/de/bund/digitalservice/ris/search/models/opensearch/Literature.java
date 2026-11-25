package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Field(name = Fields.YEARS_OF_PUBLICATION) List<String> yearsOfPublication,
    @Field(name = Fields.FIRST_PUBLICATION_DATE, type = FieldType.Date, format = DateFormat.date)
        LocalDate firstPublicationDate,
    @Field(name = Fields.DOCUMENT_TYPES) List<String> documentTypes,
    @Field(name = Fields.FUNDSTELLEN_UNSELBSTSTAENDIG) List<String> fundstellenUnselbststaendig,
    @Field(name = Fields.FUNDSTELLEN_SELBSTSTAENDIG) List<String> fundstellenSelbststaendig,
    @Field(name = Fields.NORM_REFERENCES) List<String> normReferences,
    @Nullable @Field(name = Fields.MAIN_TITLE) String mainTitle,
    @Nullable @Field(name = Fields.DOCUMENTARY_TITLE) String documentaryTitle,
    @Nullable @Field(name = Fields.MAIN_TITLE_ADDITIONS) String mainTitleAdditions,
    @Nullable @Field(name = Fields.EDITION) String edition,
    @Field(name = Fields.AUTHORS) List<String> authors,
    @Field(name = Fields.COLLABORATORS) List<String> collaborators,
    @Field(name = Fields.LANGUAGE) List<String> languages,
    @Field(name = Fields.ORIGINATOR) List<String> originators,
    @Field(name = Fields.CONFERENCE_NOTE) List<String> conferenceNotes,
    @Field(name = Fields.UNIVERSITY_NOTES) List<String> universityNotes,
    @Field(name = Fields.EDITOR) List<String> editors,
    @Field(name = Fields.FOUNDER) List<String> founder,
    @Field(name = Fields.PUBLISHER_ORGANIZATIONS) List<String> publisherOrganizations,
    @Field(name = Fields.PUBLISHER_PERSONS) List<String> publisherPersons,
    @Field(name = Fields.SHORT_TITLES) List<String> shortTitles,
    @Field(name = Fields.ADDITIONAL_TITLES) List<String> additionalTitles,
    @Field(name = Fields.FULL_TITLE_ADDITIONS) List<String> fullTitleAdditions,
    @Field(name = Fields.FOOTNOTES) List<String> footnotes,
    @Field(name = Fields.PUBLISHER_INFORMATION) List<String> publisherInformation,
    @Field(name = Fields.INTERNATIONAL_IDENTIFIERS) List<String> internationalIdentifiers,
    @Field(name = Fields.VOLUMES) List<String> volumes,
    @Field(name = Fields.TITEL_ASPEKTE) List<String> titelAspekte,
    @Field(name = Fields.SCHLAGWORTE) List<String> schlagworte,
    @Field(name = Fields.SACHGEBIETE) List<String> sachgebiete,
    @Nullable @Field(name = Fields.SHORT_REPORT) String shortReport,
    @Nullable @Field(name = Fields.OUTLINE) String outline,
    @Field(name = Fields.BIBLIOTHEKARISCHE_VERWEISUNGEN) List<String> bibliothekarischeVerweisungen,
    @Field(name = Fields.VORAUFLAGEN) List<String> vorauflagen,
    @Nullable @Field(name = Fields.KOLLATIONSVERMERK) String kollationsvermerk,
    @Field(name = Fields.REGIONEN) List<String> regionen,
    @Field(name = Fields.BERUFSBILDER) List<String> berufsbilder,
    @Field(name = Fields.DEFINITIONEN) List<String> definitionen,
    @Field(name = Fields.KUENDIGUNGSGRUENDE) List<String> kuendigungsgruende,
    @Field(name = Fields.TARIFVERTRAEGE) List<String> tarifvertraege,
    @Field(name = Fields.UEBER_KONGRESSE) List<String> ueberKongresse,
    @Field(name = Fields.UEBER_PERSONEN_INSTITUTIONEN) List<String> ueberPersonenInstitutionen,
    @Field(name = Fields.FUER_PERSONEN_INSTITUTIONEN) List<String> fuerPersonenInstitutionen,
    @Field(name = Fields.ABWEICHENDE_DOKUMENTNUMMERN) List<String> abweichendeDokumentnummern,
    @Nullable @Field(name = Fields.DOKUMENTART) String dokumentart,
    @JsonIgnore @Field(name = Fields.INDEXED_AT) String indexedAt,
    @Field(name = Fields.UNINDEXED, type = FieldType.Object, enabled = false)
        LiteratureUnindexed unindexed)
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
    public static final String FUNDSTELLEN_UNSELBSTSTAENDIG = "dependent_references";

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

    /** Stored but not indexed data, e.g. Zitierungen */
    public static final String UNINDEXED = "unindexed";

    public static final String YEARS_OF_PUBLICATION = "years_of_publication";

    public static final String TITEL_ASPEKTE = "titel_aspekte";

    public static final String SCHLAGWORTE = "schlagworte";

    public static final String SACHGEBIETE = "sachgebiete";

    public static final String BIBLIOTHEKARISCHE_VERWEISUNGEN = "bibliothekarische_verweisungen";

    public static final String VORAUFLAGEN = "vorauflagen";

    public static final String KOLLATIONSVERMERK = "kollationsvermerk";

    public static final String REGIONEN = "regionen";

    public static final String BERUFSBILDER = "berufsbilder";

    public static final String DEFINITIONEN = "definitionen";

    public static final String KUENDIGUNGSGRUENDE = "kuendigungsgruende";

    public static final String TARIFVERTRAEGE = "tarifvertraege";

    public static final String UEBER_KONGRESSE = "ueber_kongresse";

    public static final String UEBER_PERSONEN_INSTITUTIONEN = "ueber_personen_institutionen";

    public static final String FUER_PERSONEN_INSTITUTIONEN = "fuer_personen_institutionen";

    public static final String ABWEICHENDE_DOKUMENTNUMMERN = "abweichende_dokumentnummern";

    public static final String DOKUMENTART = "dokumentart";
  }
}
