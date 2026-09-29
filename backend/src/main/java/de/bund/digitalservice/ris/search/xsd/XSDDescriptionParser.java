package de.bund.digitalservice.ris.search.xsd;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.utils.StringUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parses the configured XSD schema files (and any {@code import}/{@code include}/{@code redefine}
 * they reference) into {@link DescriptionKey} entries, so that field descriptions can be sourced
 * from the XSD's {@code xs:documentation} elements instead of being duplicated in Java code.
 */
@Slf4j
public class XSDDescriptionParser {
  private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  private final XPath xPath = XPathFactory.newDefaultInstance().newXPath();
  private final Map<String, String> namespacePrefixes = new HashMap<>();

  @Getter private final Set<DescriptionKey> descriptions = new TreeSet<>();

  /**
   * Parses every XSD location configured for the {@code caselaw}, {@code adm}, and {@code
   * literature} document kinds, populating {@link #descriptions}.
   */
  public XSDDescriptionParser(XSDDescriptionProperties properties, ResourceLoader resourceLoader) {
    factory.setNamespaceAware(true);
    try {
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (ParserConfigurationException _) {
      // these features are always supported by the default DocumentBuilderFactory implementation
    }

    if (properties.getXsdLocations().containsKey("caselaw")) {
      var mainSchemaLocations = properties.getXsdLocations().get("caselaw");
      parseXSDAndCreateDescriptions(
          mainSchemaLocations, resourceLoader, properties, DocumentKind.CASE_LAW);
    }

    if (properties.getXsdLocations().containsKey("adm")) {
      var mainSchemaLocations = properties.getXsdLocations().get("adm");
      parseXSDAndCreateDescriptions(
          mainSchemaLocations, resourceLoader, properties, DocumentKind.ADMINISTRATIVE_DIRECTIVE);
    }

    if (properties.getXsdLocations().containsKey("literature")) {
      var mainSchemaLocations = properties.getXsdLocations().get("literature");
      parseXSDAndCreateDescriptions(
          mainSchemaLocations, resourceLoader, properties, DocumentKind.LITERATURE);
    }
  }

  private void parseXSDAndCreateDescriptions(
      String[] mainSchemaLocations,
      ResourceLoader resourceLoader,
      XSDDescriptionProperties properties,
      DocumentKind documentKind) {
    Map<String, Document> documents = new HashMap<>();
    for (String mainSchemaLocation : mainSchemaLocations) {
      var mainDocument = getDocumentForLocation(mainSchemaLocation, resourceLoader);

      documents.put(mainSchemaLocation, mainDocument);

      getRelatedXSD(mainDocument, resourceLoader, documents, properties.getSchemaPrefix());
    }

    Map<String, List<String>> elementsByType = new HashMap<>();
    for (Document document : documents.values()) {
      findAllElementsForType(document, elementsByType);
    }

    List<DocumentationElement> documentationElements = new ArrayList<>();
    for (Document document : documents.values()) {
      documentationElements.addAll(getDocumentationElements(document));
    }

    createDescriptions(documentationElements, elementsByType, documentKind);
  }

  private Document getDocumentForLocation(String location, ResourceLoader resourceLoader) {
    try {
      Resource resource = resourceLoader.getResource(location);
      if (!resource.exists()) {
        return null;
      }

      try (InputStream inputStream = resource.getInputStream()) {
        return factory.newDocumentBuilder().parse(inputStream);
      }
    } catch (Exception ex) {
      log.error("error by getting document for location: {}", location, ex);
      return null;
    }
  }

  private void createDescriptions(
      List<DocumentationElement> documentationElements,
      Map<String, List<String>> elementsByType,
      DocumentKind documentKind) {
    documentationElements.forEach(
        documentationElement -> {
          if (documentationElement.getParent()
              instanceof TypeElement(String namespaceUri, String name)) {
            var typeName = name;
            if (!typeName.contains(":")) {
              typeName = namespacePrefixes.get(namespaceUri) + ":" + typeName;
            }

            if (!elementsByType.containsKey(typeName)) {
              return;
            }

            elementsByType
                .get(typeName)
                .forEach(
                    elementName ->
                        descriptions.add(
                            new DescriptionKey(
                                elementName,
                                documentationElement.getDocumentation(),
                                documentationElement.getLanguage(),
                                documentKind)));
          } else {
            if (documentationElement.getParent() != null) {
              descriptions.add(
                  new DescriptionKey(
                      ((ElementElement) documentationElement.getParent()).name(),
                      documentationElement.getDocumentation(),
                      documentationElement.getLanguage(),
                      documentKind));
            }
          }
        });
  }

  private void findAllElementsForType(Document document, Map<String, List<String>> elementsByType) {
    try {
      var elements =
          (NodeList)
              xPath
                  .compile("//*[local-name()='element']")
                  .evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < elements.getLength(); i++) {
        var element = (Element) elements.item(i);
        var name = element.getAttribute("name");
        var type = element.getAttribute("type");
        if (!name.contains(":") && type.contains(":")) {
          name = type.substring(0, type.indexOf(":")) + ":" + name;
        }
        if (!elementsByType.containsKey(type)) {
          elementsByType.put(type, new ArrayList<>());
        }
        elementsByType.get(type).add(name);
      }
    } catch (XPathExpressionException e) {
      log.error("error by getting elements for type", e);
    }
  }

  private void getRelatedXSD(
      Document document,
      ResourceLoader resourceLoader,
      Map<String, Document> documents,
      String schemaPrefix) {
    extractNamespacePrefixes(document);

    try {
      var expression = xPath.compile("//*[local-name()='import']");
      var imports = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(imports, resourceLoader, documents, schemaPrefix);

      expression = xPath.compile("//*[local-name()='include']");
      var includes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(includes, resourceLoader, documents, schemaPrefix);

      expression = xPath.compile("//*[local-name()='redefine']");
      var redefines = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(redefines, resourceLoader, documents, schemaPrefix);
    } catch (XPathExpressionException e) {
      log.error("error by getting related xsd", e);
    }
  }

  private void getXSDInChildren(
      NodeList nodeList,
      ResourceLoader resourceLoader,
      Map<String, Document> documents,
      String schemaPrefix) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element importElement = (Element) nodeList.item(i);

      var schemaLocation = importElement.getAttribute("schemaLocation");
      schemaLocation = schemaPrefix + schemaLocation;
      var document = getDocumentForLocation(schemaLocation, resourceLoader);
      if (documents.containsKey(schemaLocation) || document == null) {
        continue;
      }

      documents.put(schemaLocation, document);
      getRelatedXSD(document, resourceLoader, documents, schemaPrefix);
    }
  }

  private void extractNamespacePrefixes(Document document) {
    var schema = (Element) document.getElementsByTagName("xs:schema").item(0);
    if (schema == null) {
      return;
    }

    var attributes = schema.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      var attribute = attributes.item(i);
      if (attribute.getNodeName().startsWith("xmlns:")) {
        var prefix = attribute.getNodeName().substring(6);
        var namespaceURI = attribute.getNodeValue();
        namespacePrefixes.put(namespaceURI, prefix);
      }
    }
  }

  private List<DocumentationElement> getDocumentationElements(Document document) {
    List<DocumentationElement> documentationElements = new ArrayList<>();
    String targetNamespaceURI = findTargetNamespaceURI(document);

    try {
      XPathExpression expr = xPath.compile("//*[local-name()='documentation']");
      NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

      for (int i = 0; i < nodeList.getLength(); i++) {
        Element element = (Element) nodeList.item(i);
        documentationElements.add(new DocumentationElement(element, targetNamespaceURI));
      }
    } catch (XPathExpressionException e) {
      log.error("error by getting documentation elements", e);
    }

    return documentationElements;
  }

  private String findTargetNamespaceURI(Document document) {
    var schema = (Element) document.getElementsByTagName("xs:schema").item(0);
    if (schema == null) {
      return "";
    }

    return schema.getAttribute("targetNamespace");
  }

  /**
   * Looks up the parsed XSD description for the given element/type key and language.
   *
   * @param key the XSD element/type key (e.g. "ris:dokumentnummer") to look up
   * @param language the language of the documentation text to match (e.g. "de")
   * @return the description text, if one was parsed from the XSD for that key and language
   */
  public Optional<String> findDescription(String key, String language) {
    return descriptions.stream()
        .filter(
            descriptionKey ->
                descriptionKey.key().equals(key) && descriptionKey.lang().equals(language))
        .findFirst()
        .map(DescriptionKey::description)
        .map(StringUtils::stripHtml);
  }

  /**
   * Returns all parsed descriptions belonging to the given document kind.
   *
   * @param documentKind the document kind to filter descriptions by
   * @return all descriptions parsed for that document kind
   */
  public List<DescriptionKey> getDescriptions(DocumentKind documentKind) {
    return descriptions.stream()
        .filter(descriptionKey -> descriptionKey.documentKind() == documentKind)
        .toList();
  }
}
