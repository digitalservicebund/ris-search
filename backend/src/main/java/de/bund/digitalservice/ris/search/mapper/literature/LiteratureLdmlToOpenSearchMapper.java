package de.bund.digitalservice.ris.search.mapper.literature;

import de.bund.digitalservice.ris.search.models.opensearch.DependentReference;
import de.bund.digitalservice.ris.search.models.opensearch.IndependentReference;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Person;
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
              .dependentReferences(extractDependentReferences(xmlDocument))
              .independentReferences(extractIndependentReferences(xmlDocument))
              .mainTitle(extractMainTitle(xmlDocument))
              .documentaryTitle(extractDocumentaryTitle(xmlDocument))
              .authors(extractAuthors(xmlDocument))
              .collaborators(extractCollaborators(xmlDocument))
              .shortReport(extractShortReport(xmlDocument))
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

  private static List<DependentReference> extractDependentReferences(XmlDocument xmlDocument)
      throws XPathExpressionException {
    var independentReferencesXPath =
        "//*[local-name()='otherReferences']/*[local-name()='implicitReference']/*[local-name()='fundstelleUnselbstaendig']";
    return XmlDocument.asList(xmlDocument.getNodesByXpath(independentReferencesXPath)).stream()
        .map(
            node ->
                DependentReference.builder()
                    .periodical(node.getAttributes().getNamedItem("periodikum").getTextContent())
                    .citation(node.getAttributes().getNamedItem("zitatstelle").getTextContent())
                    .build())
        .toList();
  }

  private static List<IndependentReference> extractIndependentReferences(XmlDocument xmlDocument)
      throws XPathExpressionException {
    var independentReferencesXPath =
        "//*[local-name()='otherReferences']/*[local-name()='implicitReference']/*[local-name()='fundstelleSelbstaendig']";
    return XmlDocument.asList(xmlDocument.getNodesByXpath(independentReferencesXPath)).stream()
        .map(
            node ->
                IndependentReference.builder()
                    .title(node.getAttributes().getNamedItem("titel").getTextContent())
                    .citation(node.getAttributes().getNamedItem("zitatstelle").getTextContent())
                    .build())
        .toList();
  }

  private static String extractMainTitle(XmlDocument xmlDocument) {
    return xmlDocument.getElementByXpath(
        "//*[local-name()='preface']/*[local-name()='longTitle']/*[local-name()='block' and @name='longTitle']");
  }

  private static String extractDocumentaryTitle(XmlDocument xmlDocument) {
    return xmlDocument.getElementByXpath(
        "//*[local-name()='FRBRWork']/*[local-name()='FRBRalias' and @name='dokumentarischerTitel']/@value");
  }

  private static List<Person> extractAuthors(XmlDocument xmlDocument) {
    return extractPerson(xmlDocument, "verfasser");
  }

  private static List<Person> extractCollaborators(XmlDocument xmlDocument) {
    return extractPerson(xmlDocument, "mitarbeiter");
  }

  private static String extractShortReport(XmlDocument xmlDocument) {
    return Optional.ofNullable(xmlDocument.getElementByXpath("//*[local-name()='mainBody']"))
        .map(report -> report.strip().replace("\n", "").replaceAll("\\s+", " "))
        .filter(report -> !report.isBlank())
        .orElse(null);
  }

  private static List<Person> extractPerson(XmlDocument xmlDocument, String type) {
    var personEids =
        extractNodeListTextContents(
                xmlDocument,
                String.format(
                    "//*[local-name()='FRBRWork']/*[local-name()='FRBRauthor' and @as='#%s']/@href",
                    type))
            .stream()
            .map(href -> href.replaceFirst("#", ""))
            .toList();

    return personEids.stream()
        .map(
            personEid -> {
              var personXPath =
                  String.format(
                      "//*[local-name()='references']/*[local-name()='TLCPerson' and @eId='%s']",
                      personEid);
              var personNameXpath = personXPath + "/@ris:name";
              var personTitleXpath = personXPath + "/@ris:titel";
              return Person.builder()
                  .name(xmlDocument.getElementByXpath(personNameXpath))
                  .title(xmlDocument.getElementByXpath(personTitleXpath))
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
