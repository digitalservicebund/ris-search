package de.bund.digitalservice.ris.search.legacyportal.transformer;

import de.bund.digitalservice.ris.search.legacyportal.dto.importer.ContentItemImporterDTO;
import de.bund.digitalservice.ris.search.legacyportal.dto.importer.LegislationImporterDTO;
import de.bund.digitalservice.ris.search.legacyportal.enums.LegalDocumentVersion;
import de.bund.digitalservice.ris.search.legacyportal.utils.xml.XmlParser;
import de.bund.digitalservice.xml.parser.XmlDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class LegislationImporterDTOTransformer {

  private static final String X_PATH_SHORT_TITLE_ABBREVIATION =
      "//*[local-name()='shortTitle']/*[local-name()='inline']/text()";
  private static final String X_PATH_DOC_TITLE_ABBREVIATION =
      "//*[local-name()='docTitle']/*[local-name()='inline']/text()";
  private static final String X_PATH_FBWORK_ALIAS =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRalias']/@value";
  private static final String X_PATH_FBWORK_FBURI =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRuri']/@value";
  private static final String X_PATH_FBWORK_FBNAME =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRname']/@value";
  private static final String X_PATH_FBWORK_FBNUMBER =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRnumber']/@value";
  private static final String X_PATH_FBWORK_FBDATE =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRdate']/@date";
  private static final String X_PATH_SHORT_TITLE_ALTERNATE_NAME =
      "//*[local-name()='shortTitle']/text()";
  private static final String X_PATH_DOC_TITLE_NAME = "//*[local-name()='docTitle']/text()";
  private static final String X_PATH_BLOCK_CONTAINER =
      "//*[local-name()='blockContainer']/*[local-name()='toc']/*[local-name()='tocItem']";
  private static final String X_PATH_SPAN_1 = "./span[position()=1]";
  private static final String X_PATH_SPAN_2 = "./span[position()=2]";
  private static final String X_PATH_HREF = "@href";
  private static final List<String> LIST_GLOBAL_UID_VERSION_1_4 =
      List.of(
          "2a03da3a-ab70-4c36-8a7b-8537dd95a2e2",
          "8bfd27f4-ab63-4369-a358-78e343531e93",
          "20a7d932-5542-4e65-9a3b-e436ae2755d2",
          "0445e419-7010-4927-aa13-a8ad676fface",
          "651d2e7a-c879-47bc-8586-34b910f2b2e2",
          "ae4917e1-ac6c-4bf7-bc52-3be8a7d0c929",
          "beb24312-d656-4efa-acb7-c996ee090de1",
          "c58c6a5d-9480-48c3-ba17-e8fb131848b7",
          "d3e649db-937c-4788-af0b-cf9309e83f6a",
          "e9cc28bb-0df4-42ef-a367-8cf3864fff08");

  private LegislationImporterDTOTransformer() {}

  public static LegislationImporterDTO createLegislationImporterDTO(
      XmlDocument xmlDocument, String xmlFilePath, XmlParser xmlParser) {
    return LegislationImporterDTO.builder()
        .identifier(getIdentifierByXmlDocument(xmlDocument))
        .name(getNameXmlDocument(xmlDocument))
        .documentUri(getDocumentUriByXmlDocument(xmlDocument))
        .alternateName(getAlternateNameXmlDocument(xmlDocument))
        .docTitle(getDocTitleXmlDocument(xmlDocument))
        .text(getArticlesXmlDocument(xmlDocument))
        .xmlFilePath(xmlFilePath)
        .printAnnouncementGazette(getPrintAnnouncementGazzeteByXmlDocument(xmlDocument))
        .printAnnouncementYear(getPrintAnnouncementYearByXmlDocument(xmlDocument))
        .printAnnouncementPage(getPrintAnnouncementPageByXmlDocument(xmlDocument))
        .contentItems(getTableOfContents(xmlParser))
        .globalUID(getGlobalUIDXmlDocument(xmlDocument))
        .version(getVersionXmlDocument(xmlDocument))
        .build();
  }

  private static String getIdentifierByXmlDocument(XmlDocument xmlDocument) {

    var idenfiterShortTitle = xmlDocument.getSimpleElementByXpath(X_PATH_SHORT_TITLE_ABBREVIATION);

    if (StringUtils.isNotEmpty(idenfiterShortTitle)) {
      return idenfiterShortTitle;
    }

    var idenfiterDocTitle = xmlDocument.getSimpleElementByXpath(X_PATH_DOC_TITLE_ABBREVIATION);

    if (StringUtils.isNotEmpty(idenfiterDocTitle)) {
      return idenfiterDocTitle;
    }

    return xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_ALIAS);
  }

  private static String getDocumentUriByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentDocumentUri = xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_FBURI);

    return StringUtils.isNotEmpty(xmlDocumentDocumentUri)
        ? xmlDocumentDocumentUri
        : StringUtils.EMPTY;
  }

  private static String getPrintAnnouncementGazzeteByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentPrintAnnouncementGazette =
        xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_FBNAME);

    return StringUtils.isNotEmpty(xmlDocumentPrintAnnouncementGazette)
        ? xmlDocumentPrintAnnouncementGazette
        : StringUtils.EMPTY;
  }

  private static int getPrintAnnouncementYearByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentPrintAnnouncementYear =
        xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_FBDATE);

    if (StringUtils.isEmpty(xmlDocumentPrintAnnouncementYear)
        || xmlDocumentPrintAnnouncementYear.length() < 4) {
      return NumberUtils.INTEGER_ZERO;
    }

    String printAnnouncementYearString = xmlDocumentPrintAnnouncementYear.substring(0, 4);

    return StringUtils.isNumeric(printAnnouncementYearString)
        ? NumberUtils.toInt(printAnnouncementYearString)
        : NumberUtils.INTEGER_ZERO;
  }

  private static String getPrintAnnouncementPageByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentPrintAnnouncementPage =
        xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_FBNUMBER);

    return StringUtils.isNotEmpty(xmlDocumentPrintAnnouncementPage)
        ? xmlDocumentPrintAnnouncementPage
        : StringUtils.EMPTY;
  }

  private static String getAlternateNameXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentAlternateName =
        xmlDocument.getSimpleElementByXpath(X_PATH_SHORT_TITLE_ALTERNATE_NAME);

    return StringUtils.isNotEmpty(xmlDocumentAlternateName)
        ? StringUtils.trimToNull(xmlDocumentAlternateName.replace("(", "").replace("–", ""))
        : StringUtils.EMPTY;
  }

  private static String getDocTitleXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentAlternateName = xmlDocument.getSimpleElementByXpath(X_PATH_DOC_TITLE_NAME);

    return StringUtils.isNotEmpty(xmlDocumentAlternateName)
        ? StringUtils.trimToNull(
                xmlDocumentAlternateName.replace(")", "").replace("(", "").replace("\n", " "))
            .replaceAll("\\s{2,}", " ")
        : StringUtils.EMPTY;
  }

  private static String getNameXmlDocument(XmlDocument xmlDocument) {
    List<String> nameNodes = xmlDocument.getArrayElementsByXpath("//*[local-name()='docTitle']");
    return !nameNodes.isEmpty() ? nameNodes.get(0) : StringUtils.EMPTY;
  }

  private static String getArticlesXmlDocument(XmlDocument xmlDocument) {
    List<List<String>> articles =
        xmlDocument.getArrayElementsWithChildrenByXpath("//*[local-name()='article']");
    List<String> articlesAsStrings = new ArrayList<>();
    for (List<String> article : articles) {
      articlesAsStrings.add(String.join(" ", article));
    }
    return String.join(" ", articlesAsStrings);
  }

  private static List<ContentItemImporterDTO> getTableOfContents(XmlParser xmlParser) {
    var nodeListTocItem = xmlParser.getNodeList(X_PATH_BLOCK_CONTAINER);

    if (nodeListTocItem.isEmpty()) {
      return Collections.emptyList();
    }

    return Stream.iterate(0, i -> i < nodeListTocItem.get().getLength(), i -> i + 1)
        .map(nodeListTocItem.get()::item)
        .map(
            item -> {
              var href = xmlParser.getValueItem(X_PATH_HREF, item);
              var markerNumber = xmlParser.getValueItem(X_PATH_SPAN_1, item);
              var description = xmlParser.getValueItem(X_PATH_SPAN_2, item);
              return ContentItemImporterDTO.builder()
                  .href(href)
                  .markerNumber(markerNumber)
                  .description(description)
                  .build();
            })
        .toList();
  }

  private static String getGlobalUIDXmlDocument(XmlDocument xmlDocument) {
    return xmlDocument.getSimpleElementByXpath(X_PATH_FBWORK_ALIAS);
  }

  private static String getVersionXmlDocument(XmlDocument xmlDocument) {
    var globalUIDXmlDocument = getGlobalUIDXmlDocument(xmlDocument);

    if (StringUtils.isNotEmpty(globalUIDXmlDocument)
        && LIST_GLOBAL_UID_VERSION_1_4.contains(globalUIDXmlDocument)) {
      return LegalDocumentVersion.VERSION_1_4.getVersion();
    }

    return LegalDocumentVersion.VERSION_1_6.getVersion();
  }
}
