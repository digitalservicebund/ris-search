package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LiteratureTestData {

  public static final List<Literature> allDocuments = new ArrayList<>();

  static {
    allDocuments.add(
        Literature.builder()
            .id("lit-1")
            .documentNumber("KALU000000001")
            .recordingDate(LocalDate.parse("2013-01-01"))
            .yearsOfPublication(List.of("2014", "2024"))
            .documentTypes(List.of("Auf"))
            .dependentReferences(List.of("BUV, 1982, 123-123"))
            .independentReferences(List.of("50 Jahre Betriebs-Berater, 1987, 123-456"))
            .mainTitle("Einführung in das Handelsrecht")
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
            .recordingDate(LocalDate.parse("1998-01-01"))
            .yearsOfPublication(List.of("1999", "2000", "2001"))
            .documentTypes(List.of("Kommentar", "Aufsatz"))
            .dependentReferences(List.of("NJW, 2000, 456-789"))
            .independentReferences(List.of("Festschrift für Müller, 2001, 12-34"))
            .mainTitle("Zivilprozessrecht im Wandel")
            .documentaryTitle("Dokumentation ZPO")
            .authors(List.of("Schmidt, Hans", "Becker, Anna"))
            .collaborators(List.of("Meier, Karl"))
            .shortReport("Ein Überblick über die Entwicklung der Rechtsprechung")
            .outline("Teil A: Einführung; Teil B: Praxisfälle; Teil C: Fazit")
            .build());

    allDocuments.add(
        Literature.builder()
            .id("lit-3")
            .documentNumber("KALU000000003")
            .recordingDate(LocalDate.parse("2020-01-01"))
            .yearsOfPublication(List.of("2020"))
            .documentTypes(List.of("Buch"))
            .dependentReferences(List.of())
            .independentReferences(List.of("Juristische Ausbildung, 2020, 567-890"))
            .mainTitle("Öffentliches Recht kompakt")
            .documentaryTitle(null)
            .authors(List.of("Hoffmann, Clara"))
            .collaborators(List.of())
            .shortReport("Lehrbuch für Studierende der Rechtswissenschaften")
            .outline("Teil 1: Staatsorganisationsrecht; Teil 2: Grundrechte")
            .build());
  }

  public static Literature simple(String id, String content) {
    return Literature.builder().id(id).outline(content).build();
  }
}
