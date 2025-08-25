package de.bund.digitalservice.ris.search.mapper.literature;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.utils.XmlDocument;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import literature.ldml.AkomaNtosoType;
import literature.ldml.Metadata;
import literature.ldml.OpenStructure;
import literature.ldml.ValueType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.w3c.dom.Node;

public class LiteratureLdmlToOpenSearchMapper {
  private static final Logger logger = LogManager.getLogger(LiteratureLdmlToOpenSearchMapper.class);

  private LiteratureLdmlToOpenSearchMapper() {}

  public static Optional<Literature> parseLiteratureLdml(String xmlFile) {
    try {
      var xmlDocument = XmlDocument.fromLiteratureBytes(xmlFile.getBytes(StandardCharsets.UTF_8));
      OpenStructure literatureDoc = docFromLdmlXmlString(xmlFile);
      var documentNumber = getDocumentNumber(literatureDoc);
      return Optional.of(
          Literature.builder()
              .id(documentNumber)
              .documentNumber(documentNumber)
              .yearsOfPublication(extractYearsOfPublication(literatureDoc))
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

  private static String getDocumentNumber(OpenStructure doc) {
    Optional<String> documentNumber =
        doc.getMeta().getIdentification().getFRBRExpression().getFRBRalias().stream()
            .filter(alias -> Objects.equals(alias.getName(), "documentNumber"))
            .findFirst()
            .map(ValueType::getValue);

    if (documentNumber.isPresent()) {
      return documentNumber.get();
    }

    throw new IllegalArgumentException("Literature ldml has no documentNumber");
  }

  private static List<String> extractYearsOfPublication(OpenStructure doc) {
    return doc.getMeta().getProprietary().stream()
        .map(
            prop ->
                prop.getAny().stream()
                    .filter(Metadata.class::isInstance)
                    .findFirst()
                    .map(Metadata.class::cast)
                    .orElse(null))
        .filter(Objects::nonNull)
        .findFirst()
        .map(metadata -> metadata.getVeroeffentlichungsJahre().getVeroeffentlichungsJahr())
        .orElse(Collections.emptyList());
  }

  private static List<String> extractDocumentTypes(XmlDocument xmlDocument) {
    return extractNodeListTextContents(
        xmlDocument,
        "//*[local-name()='classification' and @source='doktyp']/*[local-name()='keyword']/@value");
  }

  private static List<String> extractDependentReferences(XmlDocument xmlDocument)
      throws XPathExpressionException {
    var independentReferencesXPath =
        "//*[local-name()='otherReferences']/*[local-name()='implicitReference']/*[local-name()='fundstelleUnselbstaendig']";
    return XmlDocument.asList(xmlDocument.getNodesByXpath(independentReferencesXPath)).stream()
        .map(
            node ->
                node.getAttributes().getNamedItem("periodikum").getTextContent()
                    + StringUtils.SPACE
                    + node.getAttributes().getNamedItem("zitatstelle").getTextContent())
        .toList();
  }

  private static List<String> extractIndependentReferences(XmlDocument xmlDocument)
      throws XPathExpressionException {
    var independentReferencesXPath =
        "//*[local-name()='otherReferences']/*[local-name()='implicitReference']/*[local-name()='fundstelleSelbstaendig']";
    return XmlDocument.asList(xmlDocument.getNodesByXpath(independentReferencesXPath)).stream()
        .map(
            node ->
                node.getAttributes().getNamedItem("titel").getTextContent()
                    + StringUtils.SPACE
                    + node.getAttributes().getNamedItem("zitatstelle").getTextContent())
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

  private static List<String> extractAuthors(XmlDocument xmlDocument) {
    return extractPerson(xmlDocument, "verfasser");
  }

  private static List<String> extractCollaborators(XmlDocument xmlDocument) {
    return extractPerson(xmlDocument, "mitarbeiter");
  }

  private static String extractShortReport(XmlDocument xmlDocument) {
    return Optional.ofNullable(xmlDocument.getElementByXpath("//*[local-name()='mainBody']"))
        .map(report -> report.strip().replace("\n", "").replaceAll("\\s+", " "))
        .filter(report -> !report.isBlank())
        .orElse(null);
  }

  private static List<String> extractPerson(XmlDocument xmlDocument, String type) {
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
              var nameXpath =
                  String.format(
                      "//*[local-name()='references']/*[local-name()='TLCPerson' and @eId='%s']/@ris:name",
                      personEid);
              return xmlDocument.getElementByXpath(nameXpath);
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

  public static OpenStructure docFromLdmlXmlString(String ldmlFile) {
    try {
      StreamSource ldmlStreamSource = new StreamSource(new StringReader(ldmlFile));
      return JAXB.unmarshal(ldmlStreamSource, AkomaNtosoType.class).getDoc();
    } catch (DescriptorException | DataBindingException e) {
      throw new OpenSearchMapperException("unable to parse file to open structure", e);
    }
  }
}
