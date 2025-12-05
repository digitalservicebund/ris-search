package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
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
  @DisplayName("Should create the ELI, ID, title, abbreviation, and dates correctly")
  void shouldCreateAttributesCorrect() throws IOException {
    String normFile = "xmlContent.xml";
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).get();

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
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  @Test
  @DisplayName("Should create the official abbreviation title correct from the doctitle")
  void shouldOfficialAbbreviationTitleFromDocTitleCorrect() throws IOException {
    String normFile = "xmlDocumentTestAbbreviationWithDocTitle.xml";
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).get();
    assertEquals("AdKG", norm.getOfficialAbbreviation());
  }

  @Test
  @DisplayName("Should create the official abbreviation title correct from the short title")
  void shouldOfficialAbbreviationTitleFromShortTitleCorrect() throws IOException {
    String normFile = "xmlDocumentTestAbbreviationWithShortTitle.xml";
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).get();
    assertEquals("AdWirkG", norm.getOfficialAbbreviation());
    assertNull(norm.getNormsDate());
  }

  @Test
  @DisplayName("Should not create norms when it does not exist the eli")
  void shouldNotCreateNormsWithoutEli() throws IOException {
    String normFile = "xmlDocumentTestWithoutEli.xml";
    assertTrue(NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).isEmpty());
  }

  @Test
  @DisplayName("Should not create norms when the xml has a wrong format")
  void shouldNotCreateNormsWithWrongXmlDocument() throws IOException {
    String normFile = "xmlDocumentWrongFormat.xml";
    assertTrue(NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).isEmpty());
  }

  @Test
  @DisplayName("Should create norms date even when has an empty space in the xml file")
  void shouldCreateNormsDateEmptySpace() throws IOException {
    String normFile = "xmlDocumentTestDateWithEmptySpace.xml";
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), Map.of()).get();
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  @ParameterizedTest
  @MethodSource("getTestDates")
  void shouldCreateExpiryAndEntryIntoForceDatesCorrectly(
      String entryIntoForceDate, String expiryDate, boolean isActive) throws IOException {
    String normFile = "xmlDocumentTestEntryIntoForceAndExpiryDates.xml";
    String fileContent = String.format(readXmlTestFile(normFile), entryIntoForceDate, expiryDate);
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent, Map.of()).get();

    LocalDate expectedEntryIntoForceDate = null;
    if (entryIntoForceDate != null) {
      expectedEntryIntoForceDate = LocalDate.parse(entryIntoForceDate);
    }
    LocalDate expectedExpiryDate = null;
    if (expiryDate != null) {
      expectedExpiryDate = LocalDate.parse(expiryDate);
    }
    assertEquals(expectedEntryIntoForceDate, norm.getEntryIntoForceDate());
    assertEquals(expectedExpiryDate, norm.getExpiryDate());
  }

  private static Stream<Arguments> getTestDates() {
    return Stream.of(
        Arguments.of("1962-07-20", null, true),
        Arguments.of("1962-07-20", LocalDate.now().plusDays(1).toString(), true),
        Arguments.of(LocalDate.now().plusDays(1).toString(), null, false),
        Arguments.of(
            LocalDate.now().plusDays(1).toString(), LocalDate.now().plusDays(2).toString(), false),
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
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(fileContent, Map.of()).get();

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
  void shouldExtractArticlesCorrectly() throws IOException {
    String normFile = "xmlDocumentTestArticleExtraction.xml";
    var attachments =
        Map.of(
            "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
            readXmlTestFile("offenestruktur-1.xml"));
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), attachments).get();
    assertEquals(5, norm.getArticles().size());

    Article preamble = norm.getArticles().get(0);
    Article firstArticle = norm.getArticles().get(1);
    Article secondArticle = norm.getArticles().get(2);
    Article thirdArticle = norm.getArticles().get(3);
    Article attachment = norm.getArticles().get(4);
    assertEquals("Eingangsformel", preamble.name());
    assertEquals("Preamble", preamble.text());
    assertEquals("§ 1 Heading", norm.getArticles().get(1).name());
    assertEquals("Das ist ein Satz. Das ist noch ein Satz.", firstArticle.text());
    assertEquals("§ 2 Heading 2", secondArticle.name());
    assertEquals(
        "(1) Ein weiterer Satz mit einem Punkt in einer Aufzählung und noch einem Punkt. (2) Noch ein wichtiger Satz. Das ist der letzte Satz.",
        secondArticle.text());
    assertEquals(LocalDate.of(2003, 11, 3), firstArticle.entryIntoForceDate());
    assertNull(firstArticle.expiryDate());
    assertEquals(LocalDate.of(2003, 11, 3), secondArticle.entryIntoForceDate());
    assertEquals(LocalDate.of(2003, 11, 6), secondArticle.expiryDate());
    assertNull(thirdArticle.entryIntoForceDate());
    assertEquals(LocalDate.of(2003, 11, 1), thirdArticle.expiryDate());

    assertThat(attachment)
        .isEqualTo(
            new Article(
                "Anlage T1 (zu § 1)",
                "This text appears in the attachment. This text also appears, inside a paragraph.",
                null,
                null,
                "anlagen-1_anlage-1",
                null,
                "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
                null));
  }

  @Test
  @DisplayName("Should create the table of contents")
  void shouldCreateTheTableOfContents() throws IOException {
    String normFile = "xmlContentTestWithPreambleAndConclusionsFormula.xml";
    var attachments =
        Map.of(
            "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
            readXmlTestFile("offenestruktur-1.xml"));
    Norm norm = NormLdmlToOpenSearchMapper.parseNorm(readXmlTestFile(normFile), attachments).get();

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
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of()).orElseThrow();
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
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of()).orElseThrow();
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
    var norm = NormLdmlToOpenSearchMapper.parseNorm(sourceFile, Map.of()).orElseThrow();
    var eIds =
        List.of(
            "art-z1",
            "art-z2",
            "art-z3",
            "art-z4",
            "art-z5",
            "art-z%c2%a7%c2%a7%204%20bis%2014",
            "schluss-n1_formel-n1");
    assertThat(norm.getArticles().stream().map(Article::eId).toList()).isEqualTo(eIds);
    Article firstArticle = norm.getArticles().stream().findFirst().orElseThrow();
    assertThat(firstArticle.entryIntoForceDate()).isEqualTo(LocalDate.of(1991, 1, 1));
    assertThat(firstArticle.guid()).isEqualTo("87cd6b3a-d198-49c3-a02f-6adfd12940cb");
    assertThat(firstArticle.expiryDate()).isNull();
  }
}
