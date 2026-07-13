package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.Optional;
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

  // --------- Elis, Titles and Dates -------
  @Test
  void extractsElisTitlesAndDates() {

    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
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
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

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
    assertEquals(LocalDate.parse("2000-01-01"), norm.getEntryIntoForceDate());
    assertEquals(LocalDate.parse("2000-01-07"), norm.getExpiryDate());
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    // Should be same as in force in none-prototype env
    assertEquals(LocalDate.parse("2000-01-01"), norm.getNormsSortDate());
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  @Test
  void ignoresWhiteSpaceAroundDates() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .inForceDate(" 2000-01-01 ")
            .outOfForceDate(" 2000-01-07 ")
            .legislationDate("    1962-07-15   ")
            .datePublished("  1962-07-20   ");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    assertEquals(LocalDate.parse("2000-01-07"), norm.getExpiryDate());
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1962-07-15"), norm.getNormsDate());
    assertEquals(LocalDate.parse("1962-07-20"), norm.getDatePublished());
  }

  // --------- Tests for amtliche- and ris-abkuerzung -------
  @Test
  void setsAbbreviationToNullWhenAbbreviationsAreMissing() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation(null).risAbbreviation(null);

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getOfficialAbbreviation()).isNull();
  }

  @Test
  void prefersOfficialAbbreviationOverRisAbbreviation() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation("OffAbb").risAbbreviation("RisAbb");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getOfficialAbbreviation()).isEqualTo("OffAbb");
  }

  @Test
  void usesRisAbbreviationAsFallback() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation(null).risAbbreviation("RisAbb");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getOfficialAbbreviation()).isEqualTo("RisAbb");
  }

  // --------- Tests empty mappings ------------
  @Test
  void doesNotMapNormIfWorkEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().workEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  void doesNotMapNormIfExpressionEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().expressionEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  void doesNotMapNormIfManifestationEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().manifestationEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  void doesNotCreateNormForInvalidXmlDocument() {
    String xml = "<akn:akomaNtoso xmlns:akn=\"http://Inhaltsdaten.LegalDocML.de\"/>";
    assertTrue(NormLdmlToOpenSearchMapper.parseNorm(xml, Map.of(), true).isEmpty());
  }

  @Test
  void doesNotMapNormIfBedingtesInkrafttreten() {
    String xmlContent = NormTestDataBuilder.builder().bedingtesInkrafttreten().buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  void doesNotMapNormIfGegenstandlos() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().gegenstandslos().buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false)).isEmpty();
  }

  // -------------- Norm sort date
  @Test
  void usesInForceDateAsSortDateInNonePrototypeEnvironment() {

    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().inForceDate("2020-01-01").legislationDate("2050-07-31");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getNormsSortDate()).isEqualTo(LocalDate.parse("2020-01-01"));
  }

  @Test
  void usesLegislationDateAsSortDateInPrototypeEnvironment() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().inForceDate("2020-01-01").legislationDate("2050-07-31");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getNormsSortDate()).isEqualTo(LocalDate.parse("2050-07-31"));
  }

  // ------------- PublishedIn tests ----------------
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

  @ParameterizedTest
  @MethodSource("getTestPublishedIn")
  void shouldCreatePublishedInFieldCorrectly(
      String datePublished, String name, String number, String expectedResult) {

    // Note: The validation is disabled because null values
    // are not xsd conform for these fields
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .disableValidation()
            .frbrWork(
                frbrWork -> {
                  frbrWork.setDatePublished(datePublished).setName(name).setNumber(number);
                });

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm(xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    assertEquals(expectedResult, norm.getPublishedIn());
  }

  @Test
  void extractsArticles() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .eli("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml")
            .formula("Preamble")
            .article(
                "§ 1",
                "2003-11-03",
                null,
                "art-z1",
                article -> {
                  article
                      .addHeading("Heading 1", null)
                      .addParagraph("Das ist ein Satz. Das ist noch ein Satz.", "(1)");
                })
            .article(
                "§ 2",
                "2003-11-03",
                "2003-11-06",
                "art-z2",
                article -> {
                  article
                      .addHeading("Heading 2", null)
                      .addParagraph(
                          "Ein weiterer Satz mit einem Punkt in einer Aufzählung und noch einem Punkt.",
                          "(1)")
                      .addParagraph("Noch ein wichtiger Satz. Das ist der letzte Satz.", "(2)");
                })
            .article(
                "§ 3",
                "2003-11-01",
                null,
                "art-z3",
                article -> {
                  article.addHeading("Heading 3", null).addParagraph("Mit Text.", "");
                })
            .attachment(
                "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
                "Anlage T1",
                "(zu § 1)",
                "",
                List.of(
                    AknP.withText(
                        "This text appears in the attachment. This text also appears, inside a paragraph.")));

    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm(
            builder.buildNormXml(), builder.buildAttachmentXmls(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertEquals(5, norm.getArticles().size());

    Article preamble = norm.getArticles().get(0);
    Article firstArticle = norm.getArticles().get(1);
    Article secondArticle = norm.getArticles().get(2);
    Article thirdArticle = norm.getArticles().get(3);
    Article attachment = norm.getArticles().get(4);
    assertEquals("Eingangsformel", preamble.getName());
    assertEquals("Preamble", preamble.getText());
    assertEquals("§ 1 Heading 1", norm.getArticles().get(1).getName());
    assertEquals("(1) Das ist ein Satz. Das ist noch ein Satz.", firstArticle.getText());
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
  void createsTableOfContents() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .formula("")
            .defaultArticle()
            .conclusion("")
            .attachment(
                "eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/offenestruktur-1.xml",
                "Anlage T1",
                "(zu § 1)",
                "",
                List.of(AknP.withText("Attachemtn Content")));
    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm(xmlContent, builder.buildAttachmentXmls(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    List<TableOfContentsItem> toc = norm.getTableOfContents();

    assertThat(toc)
        .hasSize(4)
        .isEqualTo(
            List.of(
                new TableOfContentsItem("präambel-n1_formel-n1", "", "Eingangsformel", List.of()),
                new TableOfContentsItem("art-z1", "§ 1", "Article number one", List.of()),
                new TableOfContentsItem("schluss-n1_formel-n1", "", "Schlussformel", List.of()),
                new TableOfContentsItem(
                    "anlagen-n1_anlage-n1", "Anlage T1", "(zu § 1)", List.of())));
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
