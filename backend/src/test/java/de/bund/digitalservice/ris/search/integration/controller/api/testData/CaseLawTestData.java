package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

public class CaseLawTestData {
  static List<String> keywords = List.of("keywordsTest");

  public static String matchAllTerm = "Recht";

  public static List<CaseLawDocumentationUnit> allDocuments = new ArrayList<>();

  public static final int URTEIL_COUNT = 3;
  public static final int BESCHLUSS_COUNT = 1;
  public static final int OTHER_COUNT = 2;

  public static final int WITH_LEITSATZ_COUNT;
  public static final int PUBLISHED_COUNT;

  public static final String CASE_LAW_LDML_TEMPLATE = "templates/case-law/case-law-template.xml";

  static {
    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id1")
            .documentNumber("BFRE000087655")
            .ecli("ECLI:DE:FGNI:1975:0526.IXL180.73.0A")
            .courtType("KG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(SharedTestConstants.DATE_2_2)
            .fileNumbers(List.of("IX ZR 100/10"))
            .dissentingOpinion("eine abweichende Meinung")
            .decisionGrounds("diese Entscheidungsgründe")
            .headnote("Orientierungssatz")
            .headline("Test mit 1.000 € im Titel")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .otherLongText(matchAllTerm)
            .caseFacts("Tatbestand")
            .outline("outlineTest")
            .judicialBody("judicialbodyTest")
            .courtKeyword("KG Berlin")
            .keywords(keywords)
            .decisionName(List.of("decisionNames"))
            .deviatingDocumentNumber(List.of("deviatingDocumentNumbers"))
            .publicationStatus(PublicationStatus.PUBLISHED.toString())
            .documentationOffice("DS")
            .error(false)
            .legalEffect("JA")
            .build());
    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id2")
            .documentNumber("BFRE000107055")
            .ecli("ECLI:DE:FGRLP:1969:0905.IV85.68.0A")
            .courtType("FG")
            .location("Berlin")
            .courtKeyword("FG Berlin")
            .documentType("Versäumnisurteil")
            .decisionDate(SharedTestConstants.DATE_2_3)
            .guidingPrinciple("Leitsatz mit ein paar Wörtern und Ergänzungen")
            .fileNumbers(List.of("IX ZR 100/20"))
            .otherLongText("Sonstiger Langtext " + matchAllTerm)
            .otherHeadnote("Sonstiger Orientierungssatz")
            .caseFacts("Tatbestand nach § 4 TBestG")
            .publicationStatus(PublicationStatus.UNPUBLISHED.toString())
            .error(false)
            .legalEffect("JA")
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id3")
            .documentNumber("BFRE000157356")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.0")
            .courtType("FG")
            .location("Hamburg")
            .courtKeyword("FG Hamburg")
            .documentType("Zweites Versäumnisurteil")
            .fileNumbers(List.of("IX ZR 100/30"))
            .decisionDate(SharedTestConstants.DATE_2_1)
            .grounds("Folgende Gründe gibt es.")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Titelzeile")
            .publicationStatus(PublicationStatus.PUBLISHING.toString())
            .error(true)
            .procedures(List.of("proceduresTest"))
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id-b1")
            .documentNumber("BFRE000157357")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.1")
            .courtType("FG")
            .location("Gotha")
            .courtKeyword("FG Gotha")
            .documentType("Kammerbeschluss")
            .decisionDate(LocalDate.of(2025, 1, 1))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline Beschluss")
            .publicationStatus(PublicationStatus.PUBLISHED.toString())
            .error(false)
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id-e1")
            .documentNumber("BFRE000157358")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.2")
            .courtType("FG")
            .location("Hannover")
            .courtKeyword("FG Hannover")
            .documentType("Entscheidung")
            .decisionDate(LocalDate.of(2026, 1, 1))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline decision one")
            .publicationStatus(PublicationStatus.PUBLISHED.toString())
            .build());

    allDocuments.add(
        CaseLawDocumentationUnit.builder()
            .id("id-eugh1")
            .documentNumber("BFRE000157359")
            .ecli("ECLI:DE:FGHH:1972:0630.III10.72.3")
            .courtType("LG")
            .location("Saarbrücken")
            .courtKeyword("LG Saarbrücken")
            .documentType("EuGH-Vorlage")
            .decisionDate(LocalDate.of(2025, 2, 2))
            .grounds("Beschlussgründe")
            .guidingPrinciple("Leitsatz")
            .otherLongText(matchAllTerm)
            .tenor("Tenor")
            .headline("Headline EuGH-Vorlage")
            .publicationStatus(PublicationStatus.PUBLISHED.toString())
            .build());

    WITH_LEITSATZ_COUNT =
        (int)
            allDocuments.stream()
                .filter(d -> StringUtils.startsWith(d.guidingPrinciple(), "Leitsatz"))
                .count();

    PUBLISHED_COUNT =
        (int)
            allDocuments.stream()
                .filter(d -> Objects.equals(d.publicationStatus(), "PUBLISHED"))
                .count();
  }

  public static CaseLawDocumentationUnit simple(String documentNumber, String content) {
    return CaseLawDocumentationUnit.builder()
        .id(documentNumber)
        .documentNumber(documentNumber)
        .caseFacts(content)
        .build();
  }

  public static String simpleCaseLawXml(Map<String, Object> context) throws IOException {
    if (context == null) {
      context = new HashMap<>();
    }
    return SharedTestConstants.getXmlFromTemplate(context, CASE_LAW_LDML_TEMPLATE);
  }
}
