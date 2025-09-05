package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_1_1;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_2_1;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_2_2;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NormsTestData {

  public static List<Norm> allDocuments = new ArrayList<>();

  public static List<TableOfContentsItem> nestedToC =
      List.of(
          new TableOfContentsItem("art-z1", "1", "Art 1", new ArrayList<>()),
          new TableOfContentsItem("art-z2", "2", "Art 2", new ArrayList<>()),
          new TableOfContentsItem(
              "hauptteil-n1_teil-n1",
              "Teil 1",
              "Heading 1",
              List.of(
                  new TableOfContentsItem("art-z3", "3", "Art 3", new ArrayList<>()),
                  new TableOfContentsItem(
                      "hauptteil-n1_teil-n1_teil-n1",
                      "Teil 2",
                      "Heading 2",
                      List.of(
                          new TableOfContentsItem("art-z4", "4", "Art 4", new ArrayList<>()),
                          new TableOfContentsItem(
                              "hauptteil-n1_teil-n1_teil-n1_teil-n1",
                              "Teil 3",
                              "Heading 3",
                              List.of(
                                  new TableOfContentsItem(
                                      "art-z5", "5", "Art 5", new ArrayList<>()),
                                  new TableOfContentsItem(
                                      "art-z6", "6", "Art 6", new ArrayList<>()))))))));

  static {
    var normTestOne =
        Norm.builder()
            .id("n1")
            .officialAbbreviation("TeG")
            .officialTitle("Test Gesetz")
            .officialShortTitle("TestG")
            .officialShortTitle("TestG1")
            .workEli("eli/bund/bgbl-1/1000/test/regelungstext-1")
            .expressionEli("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu/regelungstext-1")
            .normsDate(DATE_2_2)
            .datePublished(DATE_2_2.plusDays(1))
            .articleTexts(List.of("example text 1", "example text 2"))
            .articleNames(List.of("§ 1 Example article", "§ 2 Example article"))
            .entryIntoForceDate(LocalDate.now().minusDays(1))
            .articles(
                List.of(
                    new Article(
                        "§ 1 Example article",
                        "example text 1",
                        LocalDate.of(2023, 12, 31),
                        LocalDate.of(3000, 1, 2),
                        "eid1",
                        "guid1",
                        null,
                        "§ 1 TeG"),
                    new Article(
                        "§ 2 Example article",
                        "example text 2",
                        null,
                        LocalDate.of(2024, 1, 2),
                        "eid2",
                        "guid2",
                        null,
                        "§ 2 TeG")))
            .tableOfContents(nestedToC)
            .build();

    var normTestTwo =
        Norm.builder()
            .id("id2")
            .tableOfContents(new ArrayList<>())
            .officialAbbreviation("TeG2")
            .officialTitle("Test Gesetz Nr. 2")
            .officialShortTitle("TestG-2")
            .officialShortTitle("TestG2")
            .workEli("eli/2024/teg/2/regelungstext-1")
            .expressionEli("eli/2024/teg/2/exp/regelungstext-1")
            .normsDate(DATE_1_1)
            .datePublished(DATE_1_1)
            .entryIntoForceDate(LocalDate.now().minusDays(1))
            .expiryDate(LocalDate.now().plusDays(1))
            .build();

    var normTestThree =
        Norm.builder()
            .id("id3")
            .tableOfContents(new ArrayList<>())
            .officialAbbreviation("TeG3")
            .officialTitle("Test Gesetz Nr. 3")
            .officialShortTitle("TestG3")
            .workEli("eli/2024/teg/3/regelungstext-1")
            .expressionEli("eli/2024/teg/3/exp/regelungstext-1")
            .normsDate(DATE_2_1)
            .datePublished(DATE_2_1)
            .expiryDate(LocalDate.now().minusDays(1))
            .build();

    allDocuments.addAll(List.of(normTestOne, normTestTwo, normTestThree));
  }

  public static Norm simple(String id, String content) {
    return Norm.builder()
        .id(id)
        .articleTexts(List.of(content))
        .articles(List.of(Article.builder().name("Article 1").text(content).build()))
        .build();
  }
}
