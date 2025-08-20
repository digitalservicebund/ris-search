package de.bund.digitalservice.ris.search.mapper.literature;

import de.bund.digitalservice.ris.search.models.opensearch.Author;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.xml.xpath.XPathExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

public class LiteratureLdmlToOpenSearchMapper {
  private static final Logger logger = LogManager.getLogger(LiteratureLdmlToOpenSearchMapper.class);

  private LiteratureLdmlToOpenSearchMapper() {}

  public static Optional<Literature> parseLiteratureLdml(String xmlFile) {
    try {
      var xmlDocument = XmlDocument.fromLiteratureBytes(xmlFile.getBytes(StandardCharsets.UTF_8));
      return Optional.of(
          Literature.builder()
              .documentNumber(extractDocumentNumber(xmlDocument))
              .yearsOfPublication(extractYearsOfPublication(xmlDocument))
              .documentTypes(extractDocumentTypes(xmlDocument))
              .mainTitle(extractMainTitle(xmlDocument))
              .documentaryTitle(extractDocumentaryTitle(xmlDocument))
              .authors(extractAuthors(xmlDocument))
              .build());
    } catch (Exception e) {
      logger.warn("Error creating literature opensearch entity.", e);
      return Optional.empty();
    }
  }

  private static String extractDocumentNumber(XmlDocument xmlDocument) {
    String documentNumber =
        xmlDocument.getElementByXpath(
            "//*[local-name()='FRBRExpression']/*[local-name()='FRBRalias' and @name='documentNumber']/@value");

    if (documentNumber == null) {
      throw new IllegalArgumentException("Literature ldml has no documentNumber");
    }

    return documentNumber;
  }

  private static List<String> extractYearsOfPublication(XmlDocument xmlDocument) {
    return extractNodeListTextContents(
        xmlDocument,
        "//*[local-name()='veroeffentlichungsJahre']/*[local-name()='veroeffentlichungsJahr']");
  }

  private static List<String> extractDocumentTypes(XmlDocument xmlDocument) {
    return extractNodeListTextContents(
        xmlDocument,
        "//*[local-name()='classification' and @source='doktyp']/*[local-name()='keyword']/@value");
  }

  private static String extractMainTitle(XmlDocument xmlDocument) {
    return xmlDocument.getElementByXpath(
        "//*[local-name()='preface']/*[local-name()='longTitle']/*[local-name()='block' and @name='longTitle']");
  }

  private static String extractDocumentaryTitle(XmlDocument xmlDocument) {
    return xmlDocument.getElementByXpath(
        "//*[local-name()='FRBRWork']/*[local-name()='FRBRalias' and @name='dokumentarischerTitel']/@value");
  }

  private static List<Author> extractAuthors(XmlDocument xmlDocument) {
    var authorEids =
        extractNodeListTextContents(
                xmlDocument,
                "//*[local-name()='FRBRWork']/*[local-name()='FRBRauthor' and @as='#verfasser']/@href")
            .stream()
            .map(href -> href.replaceFirst("#", ""))
            .toList();

    return authorEids.stream()
        .map(
            authorEid -> {
              var authorXPath =
                  String.format(
                      "//*[local-name()='references']/*[local-name()='TLCPerson' and @eId='%s']",
                      authorEid);
              var authorNameXpath = authorXPath + "/@ris:name";
              var authorTitleXpath = authorXPath + "/@ris:titel";
              return Author.builder()
                  .name(xmlDocument.getElementByXpath(authorNameXpath))
                  .title(xmlDocument.getElementByXpath(authorTitleXpath))
                  .build();
            })
        .toList();
  }

  private static List<String> extractNodeListTextContents(XmlDocument xmlDocument, String xPath) {
    List<Node> nodes;
    try {
      nodes = XmlDocument.asList(xmlDocument.getNodesByXpath(xPath));
    } catch (XPathExpressionException e) {
      return Collections.emptyList();
    }

    return nodes.stream().map(Node::getTextContent).toList();
  }
}
