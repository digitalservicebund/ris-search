package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_1_1;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_2_1;
import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_2_2;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormsTestData {

  public static final String NORM_LDML_TEMPLATE = "templates/norm/ldml-base.xml";

  public static List<TableOfContentsItem> nestedToC = setupNestedToC();

  public static List<Norm> allDocuments = setupCommonNormEntities();
  public static Map<String, String> allNormXml = setupCommonNormXmlFiles();

  private static List<TableOfContentsItem> setupNestedToC() {
    return List.of(
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
                                new TableOfContentsItem("art-z5", "5", "Art 5", new ArrayList<>()),
                                new TableOfContentsItem(
                                    "art-z6", "6", "Art 6", new ArrayList<>()))))))));
  }

  public static List<Norm> setupCommonNormEntities() {

    var normTestOne =
        Norm.builder()
            .id("n1")
            .officialAbbreviation("TeG")
            .officialTitle("Test Gesetz")
            .officialShortTitle("TestG")
            .officialShortTitle("TestG1")
            .workEli("eli/bund/bgbl-1/1000/test")
            .expressionEli("eli/bund/bgbl-1/1000/test/2000-10-06/2/deu")
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
            .workEli("eli/2024/teg/2")
            .expressionEli("eli/2024/teg/2/exp")
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
            .workEli("eli/2024/teg/3")
            .expressionEli("eli/2024/teg/3/exp")
            .normsDate(DATE_2_1)
            .datePublished(DATE_2_1)
            .expiryDate(LocalDate.now().minusDays(1))
            .build();

    return Arrays.asList(normTestOne, normTestTwo, normTestThree);
  }

  public static Norm simple(String id, String content) {
    return Norm.builder()
        .id(id)
        .articleTexts(List.of(content))
        .articles(List.of(Article.builder().name("Article 1").text(content).build()))
        .build();
  }

  public static Map<String, String> setupCommonNormXmlFiles() {
    try {
      Map<String, String> result = new HashMap<>();
      String work1 = "eli/bund/bgbl-1/1991/s102";

      String work1expression1 = work1 + "/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
      result.put(
          work1expression1,
          simpleNormXml(
              work1expression1, Map.of("inkraft", "1991-01-01", "ausserkraft", "1995-01-01")));

      String work1expression2 = work1 + "/2020-01-01/1/deu/2020-01-01/regelungstext-1.xml";
      result.put(
          work1expression2,
          simpleNormXml(
              work1expression2, Map.of("inkraft", "2020-01-01", "ausserkraft", "2049-12-31")));

      String work1expression3 = work1 + "/2050-01-01/1/deu/2050-01-01/regelungstext-1.xml";
      result.put(
          work1expression3, simpleNormXml(work1expression3, Map.of("inkraft", "2050-01-01")));

      return result;
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid norms being created at test startup.");
    }
  }

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
    return getXmlFromTemplate(context);
  }

  private static String getXmlFromTemplate(Map<String, Object> context) throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().build();
    PebbleTemplate compiledTemplate = engine.getTemplate(NORM_LDML_TEMPLATE);
    if (context == null) {
      context = new HashMap<>();
    }
    Writer writer = new StringWriter();
    compiledTemplate.evaluate(writer, context);
    return writer.toString();
  }
}
