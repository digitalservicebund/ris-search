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

/**
 * Model class representing a case law to opensearch index. This class is annotated with Lombok
 * annotations for generating getters, setters, constructors, and builder methods.
 */
@Builder
@Document(indexName = "#{@configurations.getCaseLawsIndexName()}")
public record CaseLawDocumentationUnit(
    @JsonIgnore @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Field(name = Literature.Fields.DOCUMENT_NUMBER_KEYWORD) String documentNumberKeyword,
    @Field(name = Fields.ECLI) String ecli,
    @Field(name = Fields.CELEX) String celex,
    @Field(name = Fields.CASE_FACTS) String caseFacts,
    @Field(name = Fields.DECISION_GROUNDS) String decisionGrounds,
    @Field(name = Fields.DISSENTING_OPINION) String dissentingOpinion,
    @Field(name = Fields.GROUNDS) String grounds,
    @Field(name = Fields.GUIDING_PRINCIPLE) String guidingPrinciple,
    @Field(name = Fields.HEADLINE) String headline,
    @Field(name = Fields.TITLE_LINE) String titleLine,
    @Field(name = Fields.HEADNOTE) String headnote,
    @Field(name = Fields.OTHER_HEADNOTE) String otherHeadnote,
    @Field(name = Fields.OTHER_LONG_TEXT) String otherLongText,
    @Field(name = Fields.RECHTSFRAGE_GESAMT) String rechtsfrageGesamt,
    @Field(name = Fields.TENOR, type = FieldType.Text) String tenor,
    @Field(name = Fields.DECISION_DATE, type = FieldType.Date, format = DateFormat.date)
        LocalDate decisionDate,
    @Field(name = Fields.FILE_NUMBER) String fileNumber,
    @ElementCollection @Field(name = Fields.FILE_NUMBERS) List<String> fileNumbers,
    @ElementCollection @Field(name = Fields.ABWEICHENDE_AKTENZEICHEN)
        List<String> abweichendeAktenzeichen,
    @Field(name = Fields.COURT_TYPE) String courtType,
    @Field(name = Fields.LOCATION) String location,
    @Field(name = Fields.GERICHTSBARKEIT) String gerichtsbarkeit,
    @Field(name = Fields.DOCUMENT_TYPE) String documentType,
    @Field(name = Fields.OUTLINE) String outline,
    @Field(name = Fields.JUDICIAL_BODY) String judicialBody,
    @ElementCollection @Field(name = Fields.KEYWORDS) List<String> keywords,
    @Field(name = Fields.COURT_KEYWORD) String courtKeyword,
    @ElementCollection @Field(name = Fields.DECISION_NAME) List<String> decisionName,
    @ElementCollection @Field(name = Fields.DEVIATING_DOCUMENT_NUMBER)
        List<String> deviatingDocumentNumber,
    @ElementCollection
        @Field(name = Fields.ABWEICHENDE_DATEN, type = FieldType.Date, format = DateFormat.date)
        List<LocalDate> abweichendeDaten,
    @ElementCollection @Field(name = Fields.ABWEICHENDE_ECLIS) List<String> abweichendeEclis,
    @ElementCollection @Field(name = Fields.BERUFSBILDER) List<String> berufsbilder,
    @ElementCollection @Field(name = Fields.KUENDIGUNGSARTEN) List<String> kuendigungsarten,
    @ElementCollection @Field(name = Fields.HERKUNFTSLAENDER) List<String> herkunftslaender,
    @ElementCollection @Field(name = Fields.REGIONEN) List<String> regionen,
    @ElementCollection @Field(name = Fields.TARIFVERTRAEGE) List<String> tarifvertraege,
    @ElementCollection @Field(name = Fields.KUENDIGUNGSGRUENDE) List<String> kuendigungsgruende,
    @ElementCollection @Field(name = Fields.MITWIRKENDE_RICHTER) List<String> mitwirkendeRichter,
    @ElementCollection @Field(name = Fields.SACHGEBIETE) List<String> sachgebiete,
    @ElementCollection @Field(name = Fields.STREITJAHRE) List<String> streitjahre,
    @ElementCollection @Field(name = Fields.FEHLERHAFTE_GERICHTE) List<String> fehlerhafteGerichte,
    @ElementCollection
        @Field(
            name = Fields.DATEN_DER_MUENDLICHEN_VERHANDLUNG,
            type = FieldType.Date,
            format = DateFormat.date)
        List<LocalDate> datenDerMuendlichenVerhandlung,
    @ElementCollection @Field(name = Fields.DEFINITIONEN) List<String> definitionen,
    @Field(name = Fields.ERLEDIGUNG) String erledigung,
    @Field(name = Fields.ERLEDIGUNGSVERMERK) String erledigungsvermerk,
    @Field(name = Fields.RECHTSFRAGE) String rechtsfrage,
    @Field(name = Fields.ERSTVEROEFFENTLICHUNG, type = FieldType.Date, format = DateFormat.date)
        LocalDate erstveroeffentlichung,
    @Field(name = Fields.MITTEILUNGSDATUM, type = FieldType.Date, format = DateFormat.date)
        LocalDate mitteilungsdatum,
    @JsonIgnore @Field(name = Fields.DOCUMENTATION_OFFICE) String documentationOffice,
    @Field(name = Fields.LEGAL_EFFECT) String legalEffect,
    @ElementCollection @Field(name = Fields.PREVIOUS_DECISIONS) List<String> previousDecisions,
    @ElementCollection @Field(name = Fields.ENSUING_DECISIONS) List<String> ensuingDecisions,
    @ElementCollection @Field(name = Fields.AKTIVZITIERUNG_LITERATUR_UNSELBSTAENDIG)
        List<String> aktivzitierungLiteraturUnselbstaendig,
    @ElementCollection @Field(name = Fields.PASSIVZITIERUNG_LITERATUR_UNSELBSTAENDIG)
        List<String> passivzitierungLiteraturUnselbstaendig,
    @ElementCollection @Field(name = Fields.AKTIVZITIERUNG_LITERATUR_SELBSTAENDIG)
        List<String> aktivzitierungLiteraturSelbstaendig,
    @ElementCollection @Field(name = Fields.PASSIVZITIERUNG_LITERATUR_SELBSTAENDIG)
        List<String> passivzitierungLiteraturSelbstaendig,
    @ElementCollection @Field(name = Fields.AKTIVZITIERUNG_RECHTSPRECHUNG)
        List<String> aktivzitierungRechtsprechung,
    @ElementCollection @Field(name = Fields.PASSIVZITIERUNG_RECHTSPRECHUNG)
        List<String> passivzitierungRechtsprechung,
    @ElementCollection @Field(name = Fields.AKTIVZITIERUNG_VERWALTUNGSVORSCHRIFTEN)
        List<String> aktivzitierungVerwaltungsvorschriften,
    @ElementCollection @Field(name = Fields.PASSIVZITIERUNG_VERWALTUNGSVORSCHRIFTEN)
        List<String> passivzitierungVerwaltungsvorschriften,
    @ElementCollection @Field(name = Fields.AMTLICHE_FUNDSTELLEN) List<String> amtlicheFundstellen,
    @ElementCollection @Field(name = Fields.NICHTAMTLICHE_FUNDSTELLEN)
        List<String> nichtamtlicheFundstellen,
    @ElementCollection @Field(name = Fields.GESETZESKRAFT) List<String> gesetzeskraft,
    @ElementCollection @Field(name = Fields.NORMENKETTE) List<String> normenkette,
    @JsonIgnore @ElementCollection @Field(name = Fields.PENDING_DECISIONS)
        List<String> pendingDecisions,
    @Field(name = Fields.HAS_LEGISLATIVE_MANDATE) String hasLegislativeMandate,
    @Field(name = Fields.LANGTEXTDATUM, type = FieldType.Date, format = DateFormat.date)
        LocalDate langtextdatum,
    @Field(name = Fields.RECHTSMITTELFUEHRER) String rechtsmittelfuehrer,
    @Field(name = Fields.RECHTSMITTELZULASSUNG) String rechtsmittelzulassung,
    @Field(name = Fields.REVISION) String revision,
    @Field(name = Fields.LETZTE_VEROEFFENTLICHUNG, type = FieldType.Date, format = DateFormat.date)
        LocalDate letzteVeroeffentlichung,
    @JsonIgnore @Field(name = Fields.INDEXED_AT) String indexedAt,
    @Nullable @Field(name = Fields.ARTICLES) List<Article> articles,
    @Field(name = Fields.VORABDOKUMENT) boolean vorabdokument)
    implements AbstractSearchEntity {

  /**
   * Utility class containing constant field names used for accessing and mapping the properties of
   * the CaseLawDocumentationUnit class in a record or search entity. Each constant represents the
   * name of a field within the CaseLawDocumentationUnit, which can be used for querying or
   * persisting data.
   *
   * <p>This class is not meant to be instantiated.
   */
  public static class Fields {
    private Fields() {}

    /** Field holding the court type and court location */
    public static final String COURT_KEYWORD = "court_keyword";

    public static final String COURT_KEYWORD_KEYWORD = "court_keyword.keyword";
    public static final String ID = "id";
    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String DOCUMENT_NUMBER_KEYWORD = "document_number.keyword";
    public static final String ECLI = "ecli";
    public static final String CELEX = "celex";
    public static final String ABWEICHENDE_ECLIS = "abweichende_eclis";
    public static final String ECLI_KEYWORD = "ecli.keyword";
    public static final String CASE_FACTS = "case_facts";
    public static final String DECISION_GROUNDS = "decision_grounds";
    public static final String DISSENTING_OPINION = "dissenting_opinion";
    public static final String GROUNDS = "grounds";
    public static final String GUIDING_PRINCIPLE = "guiding_principle";
    public static final String HEADLINE = "headline";
    public static final String TITLE_LINE = "title_line";
    public static final String HEADNOTE = "headnote";
    public static final String OTHER_HEADNOTE = "other_headnote";
    public static final String OTHER_LONG_TEXT = "other_long_text";
    public static final String RECHTSFRAGE_GESAMT = "rechtsfrage_gesamt";
    public static final String TENOR = "tenor";
    public static final String DECISION_DATE = "decision_date";
    public static final String FILE_NUMBER = "file_number";
    public static final String FILE_NUMBERS = "file_numbers";
    public static final String FILE_NUMBERS_KEYWORD = "file_numbers.keyword";
    public static final String ABWEICHENDE_AKTENZEICHEN = "abweichende_aktenzeichen";

    /** Field holding the type of court, e.g., FG, BVerwG */
    public static final String COURT_TYPE = "court_type";

    public static final String LOCATION = "location";
    public static final String GERICHTSBARKEIT = "gerichtsbarkeit";
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
    public static final String PREVIOUS_DECISIONS = "previous_decisions";
    public static final String ENSUING_DECISIONS = "ensuing_decisions";
    public static final String AKTIVZITIERUNG_LITERATUR_UNSELBSTAENDIG =
        "aktivzitierung_literatur_unselbstaendig";
    public static final String PASSIVZITIERUNG_LITERATUR_UNSELBSTAENDIG =
        "passivzitierung_literatur_unselbstaendig";
    public static final String AKTIVZITIERUNG_LITERATUR_SELBSTAENDIG =
        "aktivzitierung_literatur_selbstaendig";
    public static final String PASSIVZITIERUNG_LITERATUR_SELBSTAENDIG =
        "passivzitierung_literatur_selbstaendig";
    public static final String AKTIVZITIERUNG_RECHTSPRECHUNG = "aktivzitierung_rechtsprechung";
    public static final String PASSIVZITIERUNG_RECHTSPRECHUNG = "passivzitierung_rechtsprechung";
    public static final String AKTIVZITIERUNG_VERWALTUNGSVORSCHRIFTEN =
        "aktivzitierung_verwaltungsvorschriften";
    public static final String PASSIVZITIERUNG_VERWALTUNGSVORSCHRIFTEN =
        "passivzitierung_verwaltungsvorschriften";
    public static final String AMTLICHE_FUNDSTELLEN = "amtliche_fundstellen";
    public static final String NICHTAMTLICHE_FUNDSTELLEN = "nichtamtliche_fundstellen";
    public static final String GESETZESKRAFT = "gesetzeskraft";
    public static final String NORMENKETTE = "normenkette";
    public static final String PENDING_DECISIONS = "pending_decisions";
    public static final String HAS_LEGISLATIVE_MANDATE = "has_legislative_mandate";
    public static final String LANGTEXTDATUM = "langtextdatum";
    public static final String RECHTSMITTELFUEHRER = "rechtsmittelfuehrer";
    public static final String RECHTSMITTELZULASSUNG = "rechtsmittelzulassung";
    public static final String REVISION = "revision";
    public static final String LETZTE_VEROEFFENTLICHUNG = "letzte_veroeffentlichung";
    public static final String INDEXED_AT = "indexed_at";
    public static final String ARTICLES = "articles";

    public static final String ABWEICHENDE_DATEN = "abweichende_daten";
    public static final String BERUFSBILDER = "berufsbilder";
    public static final String KUENDIGUNGSARTEN = "kuendigungsarten";
    public static final String HERKUNFTSLAENDER = "herkunftslaender";
    public static final String REGIONEN = "regionen";
    public static final String TARIFVERTRAEGE = "tarifvertraege";
    public static final String KUENDIGUNGSGRUENDE = "kuendigungsgruende";
    public static final String MITWIRKENDE_RICHTER = "mitwirkende_richter";
    public static final String SACHGEBIETE = "sachgebiete";
    public static final String STREITJAHRE = "streitjahre";
    public static final String FEHLERHAFTE_GERICHTE = "fehlerhafte_gerichte";
    public static final String DATEN_DER_MUENDLICHEN_VERHANDLUNG =
        "daten_der_muendlichen_verhandlung";
    public static final String DEFINITIONEN = "definitionen";
    public static final String VORABDOKUMENT = "vorabdokument";
    public static final String ERLEDIGUNG = "erledigung";
    public static final String ERLEDIGUNGSVERMERK = "erledigungsvermerk";
    public static final String RECHTSFRAGE = "rechtsfrage";
    public static final String ERSTVEROEFFENTLICHUNG = "erstveroeffentlichung";
    public static final String MITTEILUNGSDATUM = "mitteilungsdatum";
  }
}
