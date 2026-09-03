package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.PebbleTemplateTestUtils;
import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.utils.CaseLawXmlValidator;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Strings;

/** Test data for Case Law Documentation Units. */
public class CaseLawTestData {
  static List<String> keywords = List.of("keywordsTest");

  public static String matchAllTerm = "Recht";

  public static CaseLawDocumentationUnit highlightExample =
      CaseLawDocumentationUnit.builder()
          .id("BFRE000157360")
          .documentNumber("BFRE000157360")
          .caseFacts("highlight_caseFacts")
          .decisionGrounds("highlight_decisionGrounds")
          .decisionName(List.of("highlight_decisionName"))
          .dissentingOpinion("highlight_dissentingOpinion")
          .ecli("highlight_ecli")
          .erledigungsvermerk("highlight_erledigungsvermerk")
          .fileNumbers(List.of("highlight_fileNumbers"))
          .grounds("highlight_grounds")
          .guidingPrinciple("highlight_guidingPrinciple")
          .headline("highlight_headline")
          .headnote("highlight_headnote")
          .otherHeadnote("highlight_otherHeadnote")
          .otherLongText("highlight_otherLongText")
          .outline("highlight_outline")
          .rechtsfrage("highlight_rechtsfrage")
          .rechtsfrageGesamt("highlight_rechtsfrageGesamt")
          .tenor("highlight_tenor")
          .titleLine("highlight_titleLine")
          .build();

  public static List<CaseLawDocumentationUnit> allDocuments = new ArrayList<>();

  public static final int URTEIL_COUNT = 3;
  public static final int BESCHLUSS_COUNT = 1;
  public static final int OTHER_COUNT = 2;

  public static final int WITH_LEITSATZ_COUNT;

  public static final String CASE_LAW_LDML_TEMPLATE = "templates/case-law/case-law-template.xml";

  static {
    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000087655")
            .documentNumber("BFRE000087655")
            .ecli("ECLI:DE:FGNI:1975:0526.IXL180.73.0A")
            .courtType("KG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(SharedTestConstants.DATE_2024_01_02)
            .fileNumbers(List.of("IX ZR 100/10"))
            .dissentingOpinion("eine abweichende Meinung")
            .decisionGrounds("diese Entscheidungsgründe")
            .headnote("Orientierungssatz")
            .headline("Test mit 1.000 € im Titel")
            .titleLine("Test mit 1.000 € im Titel")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .otherLongText(matchAllTerm)
            .caseFacts("Tatbestand")
            .outline("outlineTest")
            .judicialBody("judicialbodyTest")
            .courtKeyword("KG Berlin")
            .keywords(keywords)
            .decisionName(List.of("decisionNames"))
            .deviatingDocumentNumber(List.of("deviatingDocumentNumbers"))
            .legalEffect("JA")
            .celex("celexTest")
            .gerichtsbarkeit("gerichtsbarkeitTest")
            .berufsbilder(List.of("berufsbilderTest"))
            .kuendigungsarten(List.of("kuendigungsartenTest"))
            .herkunftslaender(List.of("herkunftslaenderTest"))
            .regionen(List.of("regionenTest"))
            .tarifvertraege(List.of("tarifvertraegeTest"))
            .kuendigungsgruende(List.of("kuendigungsgruendeTest"))
            .mitwirkendeRichter(List.of("mitwirkendeRichterTest"))
            .sachgebiete(List.of("sachgebieteTest"))
            .streitjahre(List.of("streitjahreTest"))
            .fehlerhafteGerichte(List.of("fehlerhafteGerichteTest"))
            .definitionen(List.of("definitionenTest"))
            .erledigung("erledigungTest")
            .erledigungsvermerk("erledigungsvermerkTest")
            .rechtsfrage("rechtsfrageTest")
            .rechtsfrageGesamt("rechtsfrageGesamtTest")
            .previousDecisions(List.of("previousDecisionsTest"))
            .ensuingDecisions(List.of("ensuingDecisionsTest"))
            .aktivzitierungRechtsprechung(List.of("aktivzitierungRechtsprechungTest"))
            .passivzitierungRechtsprechung(List.of("passivzitierungRechtsprechungTest"))
            .aktivzitierungVerwaltungsvorschriften(
                List.of("aktivzitierungVerwaltungsvorschriftenTest"))
            .passivzitierungVerwaltungsvorschriften(
                List.of("passivzitierungVerwaltungsvorschriftenTest"))
            .aktivzitierungLiteraturSelbstaendig(List.of("aktivzitierungLiteraturSelbstaendigTest"))
            .aktivzitierungLiteraturUnselbstaendig(
                List.of("aktivzitierungLiteraturUnselbstaendigTest"))
            .passivzitierungLiteraturSelbstaendig(
                List.of("passivzitierungLiteraturSelbstaendigTest"))
            .passivzitierungLiteraturUnselbstaendig(
                List.of("passivzitierungLiteraturUnselbstaendigTest"))
            .amtlicheFundstellen(List.of("amtlicheFundstellenTest"))
            .nichtamtlicheFundstellen(List.of("nichtamtlicheFundstellenTest"))
            .normenkette(List.of("normenketteTest"))
            .hasLegislativeMandate("hasLegislativeMandateTest")
            .rechtsmittelfuehrer("rechtsmittelfuehrerTest")
            .rechtsmittelzulassung("rechtsmittelzulassungTest")
            .abweichendeEclis(List.of("abweichendeEclisTest", "ECLI:DE:FGHH:1972:0630.III10.72.1"))
            .abweichendeAktenzeichen(List.of("abweichendeAktenzeichenTest"))
            .vorabdokument(true)
            .letzteVeroeffentlichung(LocalDate.of(2024, Month.MAY, 8))
            .datenDerMuendlichenVerhandlung(List.of(LocalDate.of(2024, Month.MAY, 6)))
            .langtextdatum(LocalDate.of(2024, Month.MAY, 7))
            .mitteilungsdatum(LocalDate.of(2024, Month.MAY, 10))
            .erstveroeffentlichung(LocalDate.of(2024, Month.MAY, 9))
            .abweichendeDaten(List.of(LocalDate.of(2024, Month.MAY, 5)))
            .build());
    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000107055")
            .documentNumber("BFRE000107055")
            .ecli("ECLI:DE:FGRLP:1969:0905.IV85.68.0A")
            .celex("Celex 2")
            .fileNumber("file_number_2")
            .courtType("FG")
            .location("Berlin")
            .courtKeyword("FG Berlin")
            .documentType("Versäumnisurteil")
            .decisionDate(SharedTestConstants.DATE_2024_01_03)
            .guidingPrinciple(
                "Leitsatz mit ein paar Wörtern und Ergänzungen. Auch ECLI:DE:FGHH:1972:0630.III10.72.1 drinnen.")
            .fileNumbers(List.of("IX ZR 100/20"))
            .abweichendeAktenzeichen(List.of("abweichende_aktenzeichen"))
            .otherLongText("Sonstiger Langtext " + matchAllTerm)
            .otherHeadnote("Sonstiger Orientierungssatz")
            .caseFacts("Tatbestand nach § 4 TBestG")
            .legalEffect("JA")
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000157356")
            .documentNumber("BFRE000157356")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.0")
            .abweichendeEclis(List.of("abweichendeEclisTest2"))
            .abweichendeAktenzeichen(List.of("file_number_7"))
            .courtType("FG")
            .location("Hamburg")
            .courtKeyword("FG Hamburg")
            .documentType("Zweites Versäumnisurteil")
            .fileNumbers(List.of("IX ZR 100/30"))
            .decisionDate(SharedTestConstants.DATE_2024_01_01)
            .grounds("Folgende Gründe gibt es.")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Titelzeile")
            .titleLine("Titelzeile")
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000157357")
            .documentNumber("BFRE000157357")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.1")
            .abweichendeEclis(List.of("abweichendeEclisTest3"))
            .abweichendeAktenzeichen(List.of("abweichende_aktenzeichen"))
            .courtType("FG")
            .location("Gotha")
            .courtKeyword("FG Gotha")
            .documentType("Kammerbeschluss")
            .decisionDate(LocalDate.of(2025, Month.JANUARY, 1))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz file_number_7")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline Beschluss")
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000157358")
            .documentNumber("BFRE000157358")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.2")
            .abweichendeEclis(List.of("abweichendeEclisTest4"))
            .fileNumbers(List.of("file_number_1", "file_number_5", "file_number_7"))
            .courtType("FG")
            .location("Hannover")
            .courtKeyword("FG Hannover")
            .documentType("Entscheidung")
            .decisionDate(LocalDate.of(2026, Month.JANUARY, 1))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline decision one")
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("BFRE000157359")
            .documentNumber("BFRE000157359")
            .fileNumbers(List.of("file_number_3", "file_number_4"))
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.3")
            .courtType("LG")
            .location("Saarbrücken")
            .courtKeyword("LG Saarbrücken")
            .documentType("EuGH-Vorlage")
            .decisionDate(LocalDate.of(2025, Month.FEBRUARY, 2))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline EuGH-Vorlage")
            .build());

    allDocuments.add(highlightExample);

    WITH_LEITSATZ_COUNT =
        (int)
            allDocuments.stream()
                .filter(d -> Strings.CS.startsWith(d.guidingPrinciple(), "Leitsatz"))
                .count();
  }

  /**
   * Creates a simple instance of {@code CaseLawDocumentationUnit} with specified document number
   * and content, using the builder pattern.
   *
   * @param documentNumber The unique identifier for the document.
   * @param content The case facts or content to be included in the document.
   * @return A {@code CaseLawDocumentationUnit} object populated with the given document number and
   *     content.
   */
  public static CaseLawDocumentationUnit simple(String documentNumber, String content) {
    return CaseLawDocumentationUnit.builder()
        .id(documentNumber)
        .documentNumber(documentNumber)
        .caseFacts(content)
        .build();
  }

  /**
   * Generates a simple Case Law XML based on the provided context.
   *
   * @param context A map containing key-value pairs to replace in the XML template.
   * @return A string representing the generated Case Law XML.
   * @throws IOException If there is an error reading the template file.
   */
  public static String simpleCaseLawXml(Map<String, Object> context) throws IOException {
    if (context == null) {
      context = new HashMap<>();
    }
    return PebbleTemplateTestUtils.getXmlFromTemplateWithValidation(
        context, CASE_LAW_LDML_TEMPLATE, CaseLawXmlValidator.Type.DECISION);
  }
}
