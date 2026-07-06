package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2023_01_02;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_01;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_02;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.AuthorialNote;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Test data for norms used in integration tests. */
public class NormsTestData {

  public static final String S_102_WORK_ELI = "eli/bund/bgbl-1/1991/s102";

  public static Map<String, String> s102WorkExpressions = createS102Work();
  public static Map<String, String> allNormXml = new HashMap<>(s102WorkExpressions);
  public static List<TableOfContentsItem> nestedToC = setupNestedToC();
  public static List<Norm> allNorms = setupCommonNormEntities();
  public static List<Article> allArticles = setupCommonArticleEntities();

  /**
   * Creates the S102 work with its expressions and attachments for testing purposes.
   *
   * @return map of eli file names to their XML content
   */
  private static Map<String, String> createS102Work() {
    Map<String, String> result = new HashMap<>();

    // ------------- build first expression with attachment -------------------
    String version1ExpressionEli = S_102_WORK_ELI + "/1991-01-01/1/deu";
    String version1ManifestationEli = version1ExpressionEli + "/1991-01-01/regelungstext-1.xml";
    String version1AttachmentEli = version1ExpressionEli + "/1991-01-01/anlage-regelungstext-1.xml";
    NormTestDataBuilder version1Builder =
        NormTestDataBuilder.builder()
            .eli(version1ManifestationEli)
            .inForceDate("1991-01-01")
            .outOfForceDate("1995-01-01")
            .officialTitle("Formatting Test Document", "Authorial note in the norm title.")
            .fullCitation("Verordnung zu der Vereinbarung")
            .toc(
                toc -> {
                  toc.addEntry("Abschnitt 1", "1")
                      .addEntry("Allgemeine Bestimmungen ", "2")
                      .addEntry("Eintrag 2", "1")
                      .addEntry("Art 1", "5");
                })
            .article(
                "§ 1",
                "1991-01-01",
                "1991-01-01",
                "art-z1",
                article -> {
                  article
                      .addHeading("Erster Artikel", "Authorial note in an article title.")
                      .addParagraph("Paragraph one", "(1)");
                })
            .attachment(
                version1AttachmentEli,
                "Anlage 1",
                "",
                "Anlage zum Hauptdokument",
                List.of(
                    AknP.withText("Attachment content")
                        .addChild(
                            AuthorialNote.withText("Authorial note in attachment contents"))));

    result.put(version1ManifestationEli, version1Builder.buildNormXml());
    result.put(version1AttachmentEli, version1Builder.buildAttachmentXmls().getFirst());

    // ------------- build second expression -------------------
    String version2ManifestationEli =
        S_102_WORK_ELI + "/2020-01-01/1/deu/2020-01-01/regelungstext-1.xml";
    NormTestDataBuilder version2Builder =
        NormTestDataBuilder.builder()
            .eli(version2ManifestationEli)
            .inForceDate("2020-01-01")
            .outOfForceDate("2049-12-31");

    result.put(version2ManifestationEli, version2Builder.buildNormXml());

    // ------------- build third expression -------------------
    String version3ManifestationEli =
        S_102_WORK_ELI + "/2050-01-01/1/deu/2050-01-01/regelungstext-1.xml";

    NormTestDataBuilder version3Builder =
        NormTestDataBuilder.builder().eli(version3ManifestationEli).inForceDate("2050-01-01");

    result.put(version3ManifestationEli, version3Builder.buildNormXml());

    return result;
  }

  private static TableOfContentsItem simpleToc(String number) {
    return new TableOfContentsItem("art-z" + number, number, "Art " + number, new ArrayList<>());
  }

  private static List<TableOfContentsItem> setupNestedToC() {
    return List.of(
        simpleToc("1"),
        simpleToc("2"),
        new TableOfContentsItem(
            "hauptteil-n1_teil-n1",
            "Teil 1",
            "Heading 1",
            List.of(
                simpleToc("3"),
                new TableOfContentsItem(
                    "hauptteil-n1_teil-n1_teil-n1",
                    "Teil 2",
                    "Heading 2",
                    List.of(
                        simpleToc("4"),
                        new TableOfContentsItem(
                            "hauptteil-n1_teil-n1_teil-n1_teil-n1",
                            "Teil 3",
                            "Heading 3",
                            List.of(simpleToc("5"), simpleToc("6"))))))));
  }

  /**
   * Sets up some common Article entities for testing purposes.
   *
   * @return list of common Article entities
   */
  public static List<Article> setupCommonArticleEntities() {
    return allNorms.stream()
        .map(Norm::getArticles)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .toList();
  }

  /**
   * Sets up some common Norm entities for testing purposes.
   *
   * @return list of common Norm entities
   */
  public static List<Norm> setupCommonNormEntities() {

    String workEli = "eli/bund/bgbl-1/1000/test";
    String expressionEli = workEli + "/2000-10-06/2/deu";
    String manifestationEli = expressionEli + "/2010-04-27/offenestruktur-1.xml";
    var normTestOne =
        Norm.builder()
            .id(expressionEli)
            .officialAbbreviation("TeG")
            .officialTitle("Test Gesetz")
            .officialShortTitle("TestG")
            .officialShortTitle("TestG1")
            .workEli(workEli)
            .expressionEli(expressionEli)
            .normsDate(DATE_2024_01_02)
            .datePublished(DATE_2024_01_02.plusDays(1))
            .articleTexts(List.of("example text 1", "example text 2"))
            .articleNames(List.of("§ 1 Example article", "§ 2 Example article"))
            .entryIntoForceDate(LocalDate.of(2025, Month.NOVEMBER, 1))
            .normsSortDate(LocalDate.of(2025, Month.NOVEMBER, 1))
            .articles(
                List.of(
                    new Article(
                        expressionEli + "/" + "art-z1",
                        "art-z1",
                        expressionEli,
                        workEli,
                        "§ 1 Example article",
                        "example text 1",
                        LocalDate.of(2023, Month.DECEMBER, 31),
                        LocalDate.of(3000, Month.JANUARY, 2),
                        "guid1",
                        manifestationEli,
                        "§ 1 TeG",
                        null),
                    new Article(
                        expressionEli + "/" + "art-z2",
                        "art-z2",
                        expressionEli,
                        workEli,
                        "§ 2 Example article",
                        "example text 2",
                        null,
                        LocalDate.of(2024, Month.JANUARY, 2),
                        "guid2",
                        manifestationEli,
                        "§ 2 TeG",
                        null)))
            .tableOfContents(nestedToC)
            .build();

    var normTestTwo =
        Norm.builder()
            .id("eli/2024/teg/2/exp")
            .tableOfContents(new ArrayList<>())
            .officialAbbreviation("TeG2")
            .officialTitle("Test Gesetz Nr. 2")
            .officialShortTitle("TestG-2")
            .officialShortTitle("TestG2")
            .workEli("eli/2024/teg/2")
            .expressionEli("eli/2024/teg/2/exp")
            .normsDate(DATE_2023_01_02)
            .datePublished(DATE_2023_01_02)
            .entryIntoForceDate(LocalDate.of(2025, Month.NOVEMBER, 2))
            .normsSortDate(LocalDate.of(2025, Month.NOVEMBER, 2))
            .expiryDate(LocalDate.of(2025, Month.NOVEMBER, 3))
            .build();

    var normTestThree =
        Norm.builder()
            .id("eli/2024/teg/3/exp")
            .tableOfContents(new ArrayList<>())
            .officialAbbreviation("TeG3")
            .officialTitle("Test Gesetz Nr. 3")
            .officialShortTitle("TestG3")
            .workEli("eli/2024/teg/3")
            .expressionEli("eli/2024/teg/3/exp")
            .normsDate(DATE_2024_01_01)
            .datePublished(DATE_2024_01_01)
            .entryIntoForceDate(LocalDate.of(2025, Month.NOVEMBER, 3))
            .normsSortDate(LocalDate.of(2025, Month.NOVEMBER, 3))
            .expiryDate(LocalDate.of(2025, Month.NOVEMBER, 3))
            .build();

    return new ArrayList<>(List.of(normTestOne, normTestTwo, normTestThree));
  }
}
