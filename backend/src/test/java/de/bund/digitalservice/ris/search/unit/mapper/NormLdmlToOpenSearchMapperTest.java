package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.ResourceUtils;

class NormLdmlToOpenSearchMapperTest {

  String readXmlTestFile(String fileName) throws IOException {
    File file = ResourceUtils.getFile(String.format("classpath:data/xmlTests/%s", fileName));
    return new String(Files.readAllBytes(file.toPath()));
  }

  @Test
  void testNormTestDataBuidler1() {
    NormTestDataBuilder builder =
        new NormTestDataBuilder()
            .eli("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml")
            .officialTitle("Official Title")
            .shortTitle("Short Title (", ")")
            .inForceDate("2000-01-01")
            .outOfForceDate("2000-01-07")
            .risAbbreviation("FooBar")
            .officialAbbreviation("OffFooBar")
            .attachment(
                "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/anlage-regelungstext-1.xml",
                "Anlage 1",
                "(zu § 1)",
                "Headline der Anlage",
                List.of(AknP.withText("Content of the Anlage")));

    builder.chapter(
        "Chapter One",
        "1",
        chapter1 -> {
          chapter1.addSection(
              "Section One",
              "1",
              section1 -> {
                section1.addArticle(
                    builder
                        .buildArticle(
                            "Article number one", "§ 1", "2025-01-01", "2025-07-01", "art-z1")
                        .addParagraph("Paragraph 1 test", "(1)")
                        .addParagraph("Paragraph 2 test", "(2)"));
              });

          chapter1.addSection(
              "Section Two",
              "2",
              section1 -> {
                section1.addArticle(
                    builder
                        .buildArticle(
                            "Article number two", "§ 2", "2026-01-01", "2026-07-01", "art-z2")
                        .addParagraph("Paragraph 1 test", "(1)")
                        .addParagraph("Paragraph 2 test", "(2)"));
              });
        });

    String normXml = builder.buildNormXml();
    String attachmentXml = builder.buildAttachmentXmls().getFirst();
    assertThat(normXml).isNotBlank();
    assertThat(attachmentXml).isNotBlank();
  }

  @Test
  @DisplayName("Should create the ELI, ID, title, abbreviation, and dates correctly")
  void shouldCreateAttributesCorrect() throws IOException {
    //    String xmlContent = readXmlTestFile("xmlContent.xml");

    NormTestDataBuilder builder =
        new NormTestDataBuilder()
            .eli("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml")
            .officialTitle(
                "Verordnung zur Durchführung des § 88 Abs. 2 Nr. 8 des Bundessozialhilfegesetzes")
            .shortTitle("Kurztitel (", ")")
            .officialAbbreviation("ABK")
            .inForceDate("2000-01-01")
            .outOfForceDate("2000-01-07")
            .legislationDate("1962-07-15")
            .datePublished("1962-07-20");

    String xmlContent = builder.buildNormXml();
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), true).get();

    assertEquals("eli/bund/bgbl-1/1962/s514", norm.getWorkEli());
    assertEquals("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu", norm.getExpressionEli());
    assertEquals("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu", norm.getId());
    assertEquals(
        "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml",
        norm.getManifestationEliExample());
    assertEquals(
        "Verordnung zur Durchführung des § 88 Abs. 2 Nr. 8 des Bundessozialhilfegesetzes",
        norm.getOfficialTitle());
    assertEquals("Kurztitel", norm.getOfficialShortTitle());
    assertEquals("ABK", norm.getOfficialAbbreviation());
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsSortDate());
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  @Test
  @DisplayName("Should create the official abbreviation title correct from the doctitle")
  void shouldOfficialAbbreviationTitleFromDocTitleCorrect() throws IOException {
    String normFile = "xmlDocumentTestAbbreviationWithDocTitle.xml";
    Norm norm =
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of(), true).get();
    assertEquals("AdKG", norm.getOfficialAbbreviation());
  }

  @Test
  @DisplayName("Should create the official abbreviation title correct from the short title")
  void shouldOfficialAbbreviationTitleFromShortTitleCorrect() throws IOException {
    String normFile = "xmlDocumentTestAbbreviationWithShortTitle.xml";
    Norm norm =
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of(), true).get();
    assertEquals("AdWirkG", norm.getOfficialAbbreviation());
    assertNull(norm.getNormsDate());
  }

  @Test
  @DisplayName("Should not create norms when it does not exist the eli")
  void shouldNotCreateNormsWithoutEli() throws IOException {
    String normFile = "xmlDocumentTestWithoutEli.xml";
    assertTrue(
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of(), true).isEmpty());
  }

  @Test
  @DisplayName("Should not create norms when the xml has a wrong format")
  void shouldNotCreateNormsWithWrongXmlDocument() throws IOException {
    String normFile = "xmlDocumentWrongFormat.xml";
    assertTrue(
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of(), true).isEmpty());
  }

  @Test
  @DisplayName("Should create norms date even when has an empty space in the xml file")
  void shouldCreateNormsDateEmptySpace() throws IOException {
    String normFile = "xmlDocumentTestDateWithEmptySpace.xml";
    Norm norm =
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of(), true).get();
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  @ParameterizedTest
  @MethodSource("getTestDates")
  void shouldCreateExpiryAndEntryIntoForceDatesCorrectly(
      String entryIntoForceDate, String expiryDate, boolean isActive) throws IOException {
    String normFile = "xmlDocumentTestEntryIntoForceAndExpiryDates.xml";
    String fileContent = String.format(readXmlTestFile(normFile), entryIntoForceDate, expiryDate);
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent, Map.of(), false).get();

    LocalDate expectedEntryIntoForceDate = null;
    if (entryIntoForceDate != null) {
      expectedEntryIntoForceDate = LocalDate.parse(entryIntoForceDate);
    }
    LocalDate expectedExpiryDate = null;
    if (expiryDate != null) {
      expectedExpiryDate = LocalDate.parse(expiryDate);
    }
    assertEquals(expectedEntryIntoForceDate, norm.getEntryIntoForceDate());
    assertEquals(expectedEntryIntoForceDate, norm.getNormsSortDate());
    assertEquals(expectedExpiryDate, norm.getExpiryDate());

    norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent, Map.of(), true).get();
    assertEquals(LocalDate.parse("1964-08-01"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1964-08-01"), norm.getNormsSortDate());
  }

  private static Stream<Arguments> getTestDates() {
    return Stream.of(
        Arguments.of("1962-07-20", null, true),
        Arguments.of(
            "1962-07-20", SharedTestConstants.DATE_2024_01_01.plusDays(1).toString(), true),
        Arguments.of(SharedTestConstants.DATE_2024_01_01.plusDays(1).toString(), null, false),
        Arguments.of(
            SharedTestConstants.DATE_2024_01_01.plusDays(1).toString(),
            SharedTestConstants.DATE_2024_01_01.plusDays(2).toString(),
            false),
        Arguments.of(null, null, false),
        Arguments.of("1962-07-20", "1962-07-21", false));
  }

  @ParameterizedTest
  @MethodSource("getTestPublishedIn")
  void shouldCreatePublishedInFieldCorrectly(
      String normsDate, String name, String number, String expectedResult) throws IOException {
    String normFile = "xmlDocumentTestPublishedInExtraction.xml";
    String fileContent =
        String.format(
            readXmlTestFile(normFile),
            Objects.requireNonNullElse(normsDate, ""),
            Objects.requireNonNullElse(number, ""),
            Objects.requireNonNullElse(name, ""));
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent, Map.of(), true).get();

    assertEquals(expectedResult, norm.getPublishedIn());
  }

  private static Stream<Arguments> getTestPublishedIn() {
    return Stream.of(
        Arguments.of("1962-07-20", "bgbl-1", "s120", "BGBl I, 1962 120"),
        Arguments.of(null, null, null, ""),
        Arguments.of("1962-07-20", null, null, "1962"),
        Arguments.of("1962-07-20", "bgbl-1", null, "BGBl I, 1962"),
        Arguments.of("1962-07-20", null, "s120", "1962 120"),
        Arguments.of(null, null, "s120", "120"),
        Arguments.of(null, "bgbl-1", "120", "BGBl I 120"));
  }

  @Test
  @DisplayName("Should extract the articles correctly")
  void shouldExtractArticlesCorrectly() {
    NormTestDataBuilder builder =
        new NormTestDataBuilder()
            .eli("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml")
            .formula("Preamble");

    builder
        .article(
            builder
                .buildArticle("Heading", "§ 1", "2003-11-03", null, "art-z1")
                .addParagraph("Das ist ein Satz. Das ist noch ein Satz.", ""))
        .article(
            builder
                .buildArticle("Heading 2", "§ 2", "2003-11-03", "2003-11-06", "art-z2")
                .addParagraph(
                    "Ein weiterer Satz mit einem Punkt in einer Aufzählung und noch einem Punkt.",
                    "(1)")
                .addParagraph("Noch ein wichtiger Satz. Das ist der letzte Satz.", "(2)"))
        .article(
            builder
                .buildArticle("", "", "2003-11-01", null, "art-z3")
                .addParagraph("Mit Text.", ""));

    String attachmentEli =
        "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml";
    builder.attachment(
        attachmentEli,
        "Anlage T1",
        "(zu § 1)",
        "",
        List.of(
            AknP.withText(
                "This text appears in the attachment. This text also appears, inside a paragraph.")));

    Norm norm =
        NormLdmlToOpenSearchMapper.parseNorm(
                builder.buildNormXml(),
                Map.of(attachmentEli, builder.buildAttachmentXmls().getFirst()),
                true)
            .get();

    assertEquals(5, norm.getArticles().size());

    Article preamble = norm.getArticles().get(0);
    Article firstArticle = norm.getArticles().get(1);
    Article secondArticle = norm.getArticles().get(2);
    Article thirdArticle = norm.getArticles().get(3);
    Article attachment = norm.getArticles().get(4);
    assertEquals("Eingangsformel", preamble.getName());
    assertEquals("Preamble", preamble.getText());
    assertEquals("§ 1 Heading", norm.getArticles().get(1).getName());
    assertEquals("Das ist ein Satz. Das ist noch ein Satz.", firstArticle.getText());
    assertEquals("§ 2 Heading 2", secondArticle.getName());
    assertEquals(
        "(1) Ein weiterer Satz mit einem Punkt in einer Aufzählung und noch einem Punkt. (2) Noch ein wichtiger Satz. Das ist der letzte Satz.",
        secondArticle.getText());
    assertEquals(LocalDate.of(2003, Month.NOVEMBER, 3), firstArticle.getEntryIntoForceDate());
    assertNull(firstArticle.getExpiryDate());
    assertEquals(LocalDate.of(2003, Month.NOVEMBER, 3), secondArticle.getEntryIntoForceDate());
    assertEquals(LocalDate.of(2003, Month.NOVEMBER, 6), secondArticle.getExpiryDate());
    assertEquals(LocalDate.of(2003, Month.NOVEMBER, 1), thirdArticle.getEntryIntoForceDate());
    assertNull(thirdArticle.getExpiryDate());
    String workEli = "eli/bund/bgbl-1/1962/s514";
    String expressionEli = workEli + "/2010-04-27/1/deu";
    String manifestationEli = expressionEli + "/2010-04-27/offenestruktur-1.xml";
    String eid = "anlagen-n1_anlage-n1";
    assertThat(attachment)
        .isEqualTo(
            new Article(
                expressionEli + "/" + eid,
                eid,
                expressionEli,
                workEli,
                "Anlage T1 (zu § 1)",
                "This text appears in the attachment. This text also appears, inside a paragraph.",
                null,
                null,
                null,
                manifestationEli,
                null,
                attachment.getIndexedAt()));
  }

  @Test
  @DisplayName("Should create the table of contents")
  void shouldCreateTheTableOfContents() throws IOException {
    String normFile = "xmlContentTestWithPreambleAndConclusionsFormula.xml";
    var attachments =
        Map.of(
            "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
            readXmlTestFile("offenestruktur-1.xml"));
    Norm norm =
        NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), attachments, true).get();

    List<TableOfContentsItem> toc = norm.getTableOfContents();

    assertThat(toc)
        .hasSize(4)
        .isEqualTo(
            List.of(
                new TableOfContentsItem("preambel-1_formel-1", "", "Eingangsformel", List.of()),
                new TableOfContentsItem("hauptteil-1_art-1", "§ 1", "", List.of()),
                new TableOfContentsItem("schluss-1_formel-1", "", "Schlussformel", List.of()),
                new TableOfContentsItem("anlagen-1_anlage-1", "Anlage T1", "(zu § 1)", List.of())));
  }

  List<TableOfContentsItem> expectedToC =
      List.of(
          new TableOfContentsItem(
              "hauptteil-1_teil-1",
              "Teil 1",
              "Heading 1",
              List.of(
                  new TableOfContentsItem("hauptteil-1_teil-1_para-1", "§ 1", "", List.of()),
                  new TableOfContentsItem("hauptteil-1_teil-1_para-2", "§ 2", "", List.of()))),
          new TableOfContentsItem(
              "hauptteil-1_teil-2",
              "Teil 2",
              "Heading 2",
              List.of(
                  new TableOfContentsItem(
                      "hauptteil-1_teil-2_titel-1",
                      "Titel 1",
                      "Heading 2.2",
                      List.of(
                          new TableOfContentsItem(
                              "hauptteil-1_teil-2_titel-1_para-1", "§ 3", "", List.of()))))));

  @Test
  @DisplayName("Correctly maps a nested table of content to a nested list with TableOfContentsItem")
  void nestedTableOfContentExtractionTest() throws IOException {
    var sourceFile =
        Files.readString(
            Path.of("src/test/resources/data/xmlTests/xmlDocumentTestTableOfContent.xml"));
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of(), true).orElseThrow();
    var tableOfContents = norm.getTableOfContents();
    assertEquals(tableOfContents, expectedToC);
  }

  @Test
  @DisplayName("Correctly maps a nested table of content even when article has no heading")
  void nestedTableOfContentWithoutArticleHeadingTest() throws IOException {
    var sourceFile =
        Files.readString(
            Path.of("src/test/resources/data/xmlTests/xmlDocumentTestTableOfContent.xml"));
    // Removes one empty heading node from the last article
    sourceFile =
        sourceFile.replaceAll("<[^<]*hauptteil-1_teil-2_titel-1_art-1_überschrift-1[^>]*>", "");
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of(), true).orElseThrow();
    // Table of content should be rendered the same
    var tableOfContents = norm.getTableOfContents();
    assertEquals(expectedToC, tableOfContents);
  }

  @Test
  @DisplayName("Extract articles metadata correctly")
  void extractArticlesMetadataTest() throws IOException {
    var sourceFile =
        Files.readString(
            Path.of(
                "src/test/resources/data/LDML/norm/eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml"));
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of(), true).orElseThrow();
    var eIds =
        List.of(
            "art-z1",
            "art-z2",
            "art-z3",
            "art-z4",
            "art-z5",
            "art-z%c2%a7%c2%a7%204%20bis%2014",
            "schluss-n1_formel-n1");
    assertThat(norm.getArticles().stream().map(Article::getEId).toList()).isEqualTo(eIds);
    Article firstArticle = norm.getArticles().stream().findFirst().orElseThrow();
    assertThat(firstArticle.getEntryIntoForceDate())
        .isEqualTo(LocalDate.of(1991, Month.JANUARY, 1));
    assertThat(firstArticle.getGuid()).isEqualTo("87cd6b3a-d198-49c3-a02f-6adfd12940cb");
    assertThat(firstArticle.getExpiryDate()).isNull();
  }

  private static Stream<Arguments> provideShortTitlePermutations() {
    return Stream.of(
        Arguments.of("(", ""),
        Arguments.of(" ( Kurztitel -", "Kurztitel"),
        Arguments.of("( Kurztitel -\n", "Kurztitel"),
        Arguments.of("( Kurztitel - ", "Kurztitel"),
        Arguments.of(
            "Fruchtsaft- und Erfrischungsgetränkeverordnung",
            "Fruchtsaft- und Erfrischungsgetränkeverordnung"),
        Arguments.of("Kurz-\ntitel\n", "Kurz-\ntitel"));
  }

  @ParameterizedTest
  @MethodSource("provideShortTitlePermutations")
  @DisplayName("It removes parenthesis and trailing dashes from short titles")
  void itParsesTheShortTitle(String input, String expected) {
    assertThat(NormLdmlToOpenSearchMapper.parseShortTitle(input)).isEqualTo(expected);
  }
}
