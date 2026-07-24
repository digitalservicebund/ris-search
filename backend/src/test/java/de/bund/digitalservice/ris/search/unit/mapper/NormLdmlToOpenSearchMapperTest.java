package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.search.mapper.NormLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
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

class NormLdmlToOpenSearchMapperTest {

  @Test
  @DisplayName("Extracts ELIs, titles and dates")
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
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getWorkEli()).isEqualTo("eli/bund/bgbl-1/1962/s514");
    assertThat(norm.getExpressionEli()).isEqualTo("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu");
    assertThat(norm.getId()).isEqualTo("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu");
    assertThat(norm.getManifestationEliExample())
        .isEqualTo("eli/bund/bgbl-1/1962/s514/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml");
    assertThat(norm.getOfficialTitle())
        .isEqualTo(
            "Verordnung zur Durchführung des § 88 Abs. 2 Nr. 8 des Bundessozialhilfegesetzes");
    assertThat(norm.getOfficialShortTitle()).isEqualTo("Kurztitel");
    assertThat(norm.getOfficialAbbreviation()).isEqualTo("ABK");
    assertThat(norm.getEntryIntoForceDate()).isEqualTo(LocalDate.parse("2000-01-01"));
    assertThat(norm.getExpiryDate()).isEqualTo(LocalDate.parse("2000-01-07"));
    assertThat(norm.getNormsDate()).isEqualTo(LocalDate.parse("1962-07-15"));
    // Should be same as in force in none-prototype env
    assertThat(norm.getNormsSortDate()).isEqualTo(LocalDate.parse("2000-01-01"));
    assertThat(norm.getDatePublished()).isEqualTo(LocalDate.parse("1962-07-20"));
  }

  private static Stream<Arguments> officialTitleTestData() {
    return Stream.of(
        Arguments.of("(Title)", "Title"),
        Arguments.of(" Title Title", "Title Title"),
        Arguments.of("\nTitle\n", "Title"),
        Arguments.of("        Title     ", "Title"),
        Arguments.of(" \n ( Title  \n   ) ()", "Title"),
        Arguments.of("", ""),
        Arguments.of(null, ""),
        Arguments.of("   () \n", ""));
  }

  @ParameterizedTest
  @MethodSource("officialTitleTestData")
  @DisplayName("Extracts and cleans official title")
  void extractsAndCleansOfficialTitle(String title, String expected) {

    NormTestDataBuilder builder = NormTestDataBuilder.builder().officialTitle(title);

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    assertThat(maybeNorm.get().getOfficialTitle()).isEqualTo(expected);
  }

  @Test
  @DisplayName("Ignores whitespace around dates")
  void ignoresWhiteSpaceAroundDates() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .inForceDate(" 2000-01-01 ")
            .outOfForceDate(" 2000-01-07 ")
            .legislationDate("    1962-07-15   ")
            .datePublished("  1962-07-20   ");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    assertThat(norm.getExpiryDate()).isEqualTo(LocalDate.parse("2000-01-07"));
    assertThat(norm.getNormsDate()).isEqualTo(LocalDate.parse("1962-07-15"));
    assertThat(norm.getDatePublished()).isEqualTo(LocalDate.parse("1962-07-20"));
  }

  @Test
  @DisplayName("Sets ris-abbreviation")
  void extractsRisAbbreviation() {
    NormTestDataBuilder builder = NormTestDataBuilder.builder().risAbbreviation("RisAbbrev");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getRisAbbreviation()).isEqualTo("RisAbbrev");
  }

  @Test
  @DisplayName("Does not map norm if ris-abbreviation is missing")
  void returnsEmptyOptionalIfRisAbbreviationIsMissing() {
    NormTestDataBuilder builder = NormTestDataBuilder.builder().risAbbreviation(null);

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isEmpty();
  }

  @Test
  @DisplayName("Sets abbreviation to official abbreviation if both abbreviations exist")
  void prefersOfficialAbbreviationOverRisAbbreviation() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation("OffAbb").risAbbreviation("RisAbb");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getOfficialAbbreviation()).isEqualTo("OffAbb");
  }

  @Test
  @DisplayName("Sets abbreviation to ris-abbreviation as fallback")
  void usesRisAbbreviationAsFallback() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation(null).risAbbreviation("RisAbb");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();
    assertThat(maybeNorm.get().getOfficialAbbreviation()).isEqualTo("RisAbb");
  }

  @Test
  @DisplayName("Does not map norm if no abbreviation exists")
  void returnsEmptyOptionalWhenAbbreviationsAreMissing() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().officialAbbreviation(null).risAbbreviation(null);

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isEmpty();
  }

  @Test
  @DisplayName("Does not map norm if work ELI is missing")
  void doesNotMapNormIfWorkEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().workEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  @DisplayName("Does not map norm if expression ELI is missing")
  void doesNotMapNormIfExpressionEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().expressionEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  @DisplayName("Does not map norm if manifestation ELI is missing")
  void doesNotMapNormIfManifestationEliIsMissing() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().manifestationEli(null).buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  @DisplayName("Does not create norm for invalid XML document")
  void doesNotCreateNormForInvalidXmlDocument() {
    String xml = "<akn:akomaNtoso xmlns:akn=\"http://Inhaltsdaten.LegalDocML.de\"/>";
    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xml, Map.of(), true)).isEmpty();
  }

  @Test
  @DisplayName("Does not map norm if it has a bedingtes Inkrafttreten")
  void doesNotMapNormIfBedingtesInkrafttreten() {
    String xmlContent = NormTestDataBuilder.builder().bedingtesInkrafttreten().buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  @DisplayName("Does not map norm if marked as gegenstandslos")
  void doesNotMapNormIfGegenstandlos() {
    String xmlContent =
        NormTestDataBuilder.builder().disableValidation().gegenstandslos().buildNormXml();

    assertThat(NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false)).isEmpty();
  }

  @Test
  @DisplayName("Uses in-force date as sort date outside prototype env")
  void usesInForceDateAsSortDateInNonePrototypeEnvironment() {

    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().inForceDate("2020-01-01").legislationDate("2050-07-31");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getNormsSortDate()).isEqualTo(LocalDate.parse("2020-01-01"));
  }

  @Test
  @DisplayName("Uses legislation date as sort date in prototype env")
  void usesLegislationDateAsSortDateInPrototypeEnvironment() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder().inForceDate("2020-01-01").legislationDate("2050-07-31");

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm = NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getNormsSortDate()).isEqualTo(LocalDate.parse("2050-07-31"));
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

  @ParameterizedTest
  @MethodSource("getTestPublishedIn")
  @DisplayName("Builds the published-in field correctly")
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
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    assertThat(norm.getPublishedIn()).isEqualTo(expectedResult);
  }

  @Test
  @DisplayName("Extracts articles, preamble and attachments")
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
            "", builder.buildNormXml(), builder.buildAttachmentXmls(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();

    assertThat(norm.getArticles()).hasSize(5);

    Article preamble = norm.getArticles().get(0);
    Article firstArticle = norm.getArticles().get(1);
    Article secondArticle = norm.getArticles().get(2);
    Article thirdArticle = norm.getArticles().get(3);
    Article attachment = norm.getArticles().get(4);

    assertThat(preamble.getName()).isEqualTo("Eingangsformel");
    assertThat(preamble.getText()).isEqualTo("Preamble");
    assertThat(firstArticle.getEntryIntoForceDate())
        .isEqualTo(LocalDate.of(2003, Month.NOVEMBER, 3));
    assertThat(firstArticle.getExpiryDate()).isNull();

    assertThat(firstArticle.getName()).isEqualTo("§ 1 Heading 1");
    assertThat(firstArticle.getText()).isEqualTo("(1) Das ist ein Satz. Das ist noch ein Satz.");

    assertThat(secondArticle.getName()).isEqualTo("§ 2 Heading 2");
    assertThat(secondArticle.getText())
        .isEqualTo(
            "(1) Ein weiterer Satz mit einem Punkt in einer Aufzählung und noch einem Punkt. (2) Noch ein wichtiger Satz. Das ist der letzte Satz.");
    assertThat(secondArticle.getEntryIntoForceDate())
        .isEqualTo(LocalDate.of(2003, Month.NOVEMBER, 3));
    assertThat(secondArticle.getExpiryDate()).isEqualTo(LocalDate.of(2003, Month.NOVEMBER, 6));

    assertThat(thirdArticle.getEntryIntoForceDate())
        .isEqualTo(LocalDate.of(2003, Month.NOVEMBER, 1));
    assertThat(thirdArticle.getExpiryDate()).isNull();

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
  @DisplayName("Creates table of contents with attachment")
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
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, builder.buildAttachmentXmls(), false);

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

  @Test
  @DisplayName("Creates nested table of contents with chapters and sections")
  void createsNestedTableOfContents() {
    NormTestDataBuilder builder = NormTestDataBuilder.builder();

    builder
        .chapter(
            "Heading 1",
            "Kapitel 1",
            chapter -> {
              chapter
                  .addArticle(
                      builder
                          .buildArticle("§ 1", "2020-01-01", null, "art-z1")
                          .addHeading("Artikel 1", null)
                          .addParagraph("Paragraf 1", "(1)"))
                  .addArticle(
                      builder
                          .buildArticle("§ 2", "2020-01-01", null, "art-z2")
                          .addHeading("", null)
                          .addParagraph("Paragraf 1", "(1)"));
            })
        .chapter(
            "Heading 2",
            "Kapitel 2",
            chapter -> {
              chapter.setEId("kapitel-n2");
              chapter.addSection(
                  "Heading 2.1",
                  "Abschnitt 2.1",
                  section -> {
                    section.addArticle(
                        builder
                            .buildArticle("§ 3", "2020-01-01", null, "art-z3")
                            .addHeading("Artikel 3", null)
                            .addParagraph("Paragraf 1", "(1)"));
                  });
            });

    String xmlContent = builder.buildNormXml();
    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm("", xmlContent, Map.of(), false);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    var tableOfContents = norm.getTableOfContents();
    assertThat(tableOfContents)
        .isEqualTo(
            List.of(
                new TableOfContentsItem(
                    "kapitel-n1",
                    "Kapitel 1",
                    "Heading 1",
                    List.of(
                        new TableOfContentsItem("art-z1", "§ 1", "Artikel 1", List.of()),
                        new TableOfContentsItem("art-z2", "§ 2", "", List.of()))),
                new TableOfContentsItem(
                    "kapitel-n2",
                    "Kapitel 2",
                    "Heading 2",
                    List.of(
                        new TableOfContentsItem(
                            "abschnitt-n1",
                            "Abschnitt 2.1",
                            "Heading 2.1",
                            List.of(
                                new TableOfContentsItem(
                                    "art-z3", "§ 3", "Artikel 3", List.of())))))));
  }

  @Test
  @DisplayName("Extract articles metadata correctly")
  void extractArticlesMetadataTest() {
    NormTestDataBuilder builder =
        NormTestDataBuilder.builder()
            .formula("Preamble")
            .article(
                "§ 1",
                "2003-11-03",
                "2004-05-12",
                "art-z1",
                article -> {
                  article.addHeading("Heading 1", null).addParagraph("Article content 1", "(1)");
                })
            .article(
                "Article with weird name",
                "2003-11-03",
                null,
                "art-z%c2%a7%c2%a7%204%20bis%2014",
                article -> {
                  article.addHeading("Heading 2", null).addParagraph("Article content 2", "(1)");
                })
            .conclusion("Conclusion");

    Optional<Norm> maybeNorm =
        NormLdmlToOpenSearchMapper.parseNorm(
            "", builder.buildNormXml(), builder.buildAttachmentXmls(), true);

    assertThat(maybeNorm).isNotEmpty();

    Norm norm = maybeNorm.get();
    var eIds =
        List.of(
            "präambel-n1_formel-n1",
            "art-z1",
            "art-z%c2%a7%c2%a7%204%20bis%2014",
            "schluss-n1_formel-n1");
    assertThat(norm.getArticles().stream().map(Article::getEId).toList()).isEqualTo(eIds);

    Article firstArticle = norm.getArticles().get(1);
    assertThat(firstArticle.getEntryIntoForceDate())
        .isEqualTo(LocalDate.of(2003, Month.NOVEMBER, 3));
    assertThat(firstArticle.getExpiryDate()).isEqualTo(LocalDate.of(2004, Month.MAY, 12));

    Article secondArticle = norm.getArticles().get(2);
    assertThat(secondArticle.getExpiryDate()).isNull();
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
