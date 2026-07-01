package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2023_01_02;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_01;
import static de.bund.digitalservice.ris.SharedTestConstants.DATE_2024_01_02;

import de.bund.digitalservice.ris.PebbleTemplateTestUtils;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.io.IOException;
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

  public static final String NORM_LDML_TEMPLATE = "templates/norm/norm-template.xml";
  public static final String NORM_ATTACHMENT_LDML_TEMPLATE =
      "templates/norm/norm-attachment-template.xml";
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
  public static Map<String, String> createS102Work() {
    try {
      Map<String, String> result = new HashMap<>();

      String work1expression1attachment1 =
          S_102_WORK_ELI + "/1991-01-01/1/deu/1991-01-01/anlage-regelungstext-1.xml";
      result.put(
          work1expression1attachment1, simpleNormXmlAttachment(work1expression1attachment1, null));

      String work1expression1 = S_102_WORK_ELI + "/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
      result.put(
          work1expression1,
          simpleNormXml(
              work1expression1,
              Map.of(
                  "inkraft",
                  "1991-01-01",
                  "ausserkraft",
                  "1995-01-01",
                  "attachment",
                  work1expression1attachment1)));

      String work1expression2 = S_102_WORK_ELI + "/2020-01-01/1/deu/2020-01-01/regelungstext-1.xml";
      result.put(
          work1expression2,
          simpleNormXml(
              work1expression2, Map.of("inkraft", "2020-01-01", "ausserkraft", "2049-12-31")));

      String work1expression3 = S_102_WORK_ELI + "/2050-01-01/1/deu/2050-01-01/regelungstext-1.xml";
      result.put(
          work1expression3, simpleNormXml(work1expression3, Map.of("inkraft", "2050-01-01")));

      return result;
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid norms being created at test startup.");
    }
  }

  /**
   * Creates a simple Norm XML from a template for testing purposes.
   *
   * @param fileName the eli file name
   * @param context additional context variables for the template
   * @return the generated XML as string
   * @throws IOException if template reading fails
   */
  public static String simpleNormXml(String fileName, Map<String, Object> context)
      throws IOException {
    if (context == null) {
      context = new HashMap<>();
    }
    context = new HashMap<>(context);
    EliFile eliFile =
        EliFile.fromString(fileName)
            .orElseThrow(() -> new IllegalArgumentException("Invalid eli file"));
    context.put("work_eli", eliFile.getWorkEli().toString());
    context.put("expression_eli", eliFile.getExpressionEli().toString());
    context.put("manifestation_eli", eliFile.getManifestationEli().toString());
    return PebbleTemplateTestUtils.getXmlFromTemplate(context, NORM_LDML_TEMPLATE);
  }

  /**
   * Creates a simple Norm XML attachment from a template for testing purposes.
   *
   * @param fileName the eli file name
   * @param context additional context variables for the template
   * @return the generated XML as string
   * @throws IOException if template reading fails
   */
  public static String simpleNormXmlAttachment(String fileName, Map<String, Object> context)
      throws IOException {
    if (context == null) {
      context = new HashMap<>();
    }
    context = new HashMap<>(context);
    EliFile eliFile =
        EliFile.fromString(fileName)
            .orElseThrow(() -> new IllegalArgumentException("Invalid eli file"));
    context.put("work_eli", eliFile.getWorkEli().toString());
    context.put("expression_eli", eliFile.getExpressionEli().toString());
    context.put("attachment_name", eliFile.fileName());
    return PebbleTemplateTestUtils.getXmlFromTemplate(context, NORM_ATTACHMENT_LDML_TEMPLATE);
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
            .abbreviation("TeG")
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
            .abbreviation("TeG2")
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
            .abbreviation("TeG3")
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
