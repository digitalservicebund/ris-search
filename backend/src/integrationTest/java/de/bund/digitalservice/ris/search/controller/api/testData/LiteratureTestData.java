package de.bund.digitalservice.ris.search.controller.api.testData;

import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/** Test data for Literature documents. */
public class LiteratureTestData {

  public static String matchAllTerm = "Literatur. ";

  public static final List<Literature> allDocuments = new ArrayList<>();

  static {
    allDocuments.add(
        Literature.builder()
            .id("lit-1")
            .documentNumber("KALU000000001")
            .yearsOfPublication(List.of("2014", "2024"))
            .firstPublicationDate(LocalDate.of(2014, Month.JANUARY, 1))
            .documentTypes(List.of("Auf"))
            .fundstellenUnselbststaendig(List.of("BUV, 1982, 123-123"))
            .fundstellenSelbststaendig(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .mainTitle(matchAllTerm + "Einführung in das Handelsrecht")
            .documentaryTitle("Dokumentation Handelsrecht")
            .authors(List.of("Musterfrau, Sabine"))
            .collaborators(List.of("Mustermann, Max"))
            .shortReport("Kurzer Bericht über die Entwicklung des Handelsrechts")
            .outline("Kapitel 1: Grundlagen; Kapitel 2: Beispiele")
            .build());

    allDocuments.add(
        Literature.builder()
            .id("lit-2")
            .documentNumber("KALU000000002")
            .yearsOfPublication(List.of("1999", "2000", "2001"))
            .firstPublicationDate(LocalDate.of(1999, Month.JANUARY, 1))
            .documentTypes(List.of("Kommentar", "Aufsatz"))
            .fundstellenUnselbststaendig(List.of("NJW, 2000, 456-789"))
            .fundstellenSelbststaendig(List.of("Festschrift für Müller, 2001, 12-34"))
            .normReferences(List.of("GG, Art 6 Abs 2 S 1, 1949-05-23"))
            .mainTitle(matchAllTerm + "Zivilprozessrecht im Wandel")
            .mainTitleAdditions("Zusatz zu Zivilprozessrecht im Wandel")
            .documentaryTitle("Dokumentation ZPO")
            .authors(List.of("Schmidt, Hans", "Becker, Anna"))
            .collaborators(List.of("Meier, Karl"))
            .originators(List.of("FOO"))
            .conferenceNotes(List.of("Internationaler Kongress 2025, Berlin, GER"))
            .languages(List.of("deu", "eng"))
            .shortReport("Ein Überblick über die Entwicklung der Rechtsprechung")
            .outline("Teil A: Einführung; Teil B: Praxisfälle; Teil C: Fazit")
            .build());

    allDocuments.add(
        Literature.builder()
            .id("lit-3")
            .documentNumber("KALU000000003")
            .yearsOfPublication(List.of("2020"))
            .firstPublicationDate(LocalDate.of(2020, Month.JANUARY, 1))
            .documentTypes(List.of("Buch"))
            .fundstellenUnselbststaendig(List.of())
            .fundstellenSelbststaendig(List.of("Juristische Ausbildung, 2020, 567-890"))
            .mainTitle(matchAllTerm + "Öffentliches Recht kompakt")
            .documentaryTitle(null)
            .authors(List.of("Hoffmann, Clara"))
            .collaborators(List.of())
            .shortReport("Lehrbuch für Studierende der Rechtswissenschaften")
            .outline("Teil 1: Staatsorganisationsrecht; Teil 2: Grundrechte")
            .shortTitles(List.of("kurztitelTest"))
            .additionalTitles(List.of("nebentitelTest"))
            .fullTitleAdditions(List.of("gesamttitelTest"))
            .footnotes(List.of("fussnoteTest"))
            .publisherPersons(List.of("herausgeberPersonTest"))
            .publisherOrganizations(List.of("herausgeberInstitutionTest"))
            .editors(List.of("bearbeiterTest"))
            .founder(List.of("begruenderTest"))
            .internationalIdentifiers(List.of("isbnTest"))
            .edition("ausgabeTest")
            .volumes(List.of("teilbandTest"))
            .publisherInformation(List.of("verlagTest"))
            .universityNotes(List.of("hochschulvermerkTest"))
            .titelAspekte(List.of("titelAspektTest"))
            .bibliothekarischeVerweisungen(List.of("zitformalTest"))
            .vorauflagen(List.of("vorauflageTest"))
            .kollationsvermerk("kollationsvermerkTest")
            .schlagworte(List.of("schlagwortTest"))
            .sachgebiete(List.of("sachgebietTest"))
            .regionen(List.of("regionTest"))
            .berufsbilder(List.of("berufsbildTest"))
            .definitionen(List.of("definitionTest"))
            .kuendigungsgruende(List.of("kuendigungsgrundTest"))
            .tarifvertraege(List.of("tarifvertragTest"))
            .ueberPersonenInstitutionen(List.of("ueberPersonTest"))
            .ueberKongresse(List.of("ueberKongressTest"))
            .fuerPersonenInstitutionen(List.of("fuerPersonTest"))
            .abweichendeDokumentnummern(List.of("abweichendeNummerTest"))
            .dokumentart("dokumentartTest")
            .aktivzitierungenRechtsprechung(List.of("zitierungRechtsprechungTest"))
            .aktivzitierungenLiteraturSelbstaendig(List.of("zitierungSelbstaendigTest"))
            .aktivzitierungenLiteraturUnselbstaendig(List.of("zitierungUnselbstaendigTest"))
            .aktivzitierungenVerwaltungsvorschriften(List.of("zitierungVerwaltungTest"))
            .build());
  }

  /**
   * Creates a simple instance of the Literature class based on the provided document number and
   * content.
   *
   * @param documentNumber the unique identifier for the document
   * @param content the content to be set as the outline of the Literature instance
   * @return a new instance of Literature with the specified document number and outline
   */
  public static Literature simple(String documentNumber, String content) {
    return Literature.builder()
        .id(documentNumber)
        .documentNumber(documentNumber)
        .outline(content)
        .build();
  }
}
