package de.bund.digitalservice.ris.search.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * Parses the {@code ris} namespace's XSD schema (and any {@code import}/{@code include}/{@code
 * redefine} it references) into {@link DescriptionKey} entries, so that field descriptions can be
 * sourced from the XSD's {@code xs:documentation} elements instead of being duplicated in Java
 * code, and into {@link GlossaryEntry} entries for a human-readable glossary of the whole
 * namespace.
 */
@Slf4j
public class XSDDescriptionParser {
  private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  private final XPath xPath = XPathFactory.newDefaultInstance().newXPath();
  private final Map<String, String> namespacePrefixes = new HashMap<>();

  @Getter private final Set<DescriptionKey> descriptions = new TreeSet<>();
  @Getter private final List<GlossaryEntry> glossaryEntries = new ArrayList<>();

  /** Parses the configured {@code ris} namespace location, populating {@link #descriptions}. */
  public XSDDescriptionParser(XSDDescriptionProperties properties, ResourceLoader resourceLoader) {
    factory.setNamespaceAware(true);
    try {
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (ParserConfigurationException _) {
      // these features are always supported by the default DocumentBuilderFactory implementation
    }

    // A missing or broken schema must not prevent the application from starting: field
    // descriptions are then simply not available, and @RISSchema falls back to its own text.
    var mainLocation = properties.getRisNamespaceLocation();
    if (mainLocation == null || mainLocation.isBlank()) {
      log.error("xsd.ris-namespace-location is not set; no XSD descriptions will be available");
      return;
    }
    var resource = resourceLoader.getResource(mainLocation);
    var mainDocument = getDocumentForLocation(resource);
    if (mainDocument == null) {
      log.error(
          "could not load XSD schema {}; no XSD descriptions will be available", mainLocation);
      return;
    }

    // Insertion-ordered, so that schema files are always processed in the same (include/import
    // traversal) order. Keyed by the resource description, like included files, so that a file
    // that includes the root schema again is recognized as already parsed.
    Map<String, Document> documents = new LinkedHashMap<>();
    documents.put(resource.getDescription(), mainDocument);
    getRelatedXSD(mainDocument, resource, documents);

    Map<String, List<String>> elementsByType = new HashMap<>();
    for (Document document : documents.values()) {
      findAllElementsForType(document, elementsByType);
    }

    createDescriptions(documents, elementsByType);
  }

  private Document getDocumentForLocation(Resource resource) {
    try {
      if (!resource.exists()) {
        return null;
      }

      try (InputStream inputStream = resource.getInputStream()) {
        return factory.newDocumentBuilder().parse(inputStream);
      }
    } catch (Exception ex) {
      log.error("error by getting document for resource: {}", resource, ex);
      return null;
    }
  }

  /**
   * One documented definition found for a key: its description texts by language (e.g. {@code
   * "de"}, {@code "en"}), and whether it comes from the type's own name (authoritative) or from an
   * element bound to that type (an alias).
   */
  private record Candidate(Map<String, String> byLang, boolean authoritative) {}

  private void createDescriptions(
      Map<String, Document> documents, Map<String, List<String>> elementsByType) {
    // key -> every candidate found for it. Several distinct types can share the same bare element
    // name (e.g. a "titel" element may be bound to ris:personTitel in one place and
    // ris:gesamttitelTitel in another), so all candidates are collected first and each key is
    // then resolved once, independently of the order in which its candidates were found. Both the
    // `descriptions` lookup used by @RISSchema and the glossary are built from that one result.
    Map<String, List<Candidate>> candidatesByKey = new LinkedHashMap<>();

    for (Document document : documents.values()) {
      if (document == null) {
        continue;
      }

      // Group documentation elements by their shared parent, so that sibling xml:lang="de"/"en"
      // annotations on the same type or element are paired up into a single occurrence instead
      // of being processed as independent, language-only entries.
      Map<XSDElement, Map<String, String>> byLangPerParent = new LinkedHashMap<>();
      for (DocumentationElement documentationElement : getDocumentationElements(document)) {
        var parent = documentationElement.getParent();
        if (parent == null) {
          continue;
        }
        byLangPerParent
            .computeIfAbsent(parent, _ -> new LinkedHashMap<>())
            .put(documentationElement.getLanguage(), documentationElement.getDocumentation());
      }

      byLangPerParent.forEach((parent, byLang) -> {
        if (!(parent instanceof TypeElement(String namespaceUri, String name))) {
          // Local annotations on generic element refs (e.g. akn:motivation) are intentionally
          // not registered here: only descriptions for named ris:<typeName> types are
          // supported.
          return;
        }

        var typeName =
            name.contains(":") ? name : String.join(":", namespacePrefixes.get(namespaceUri), name);

        // Some named types (e.g. ris:rechtsfrageGesamt) are only ever selected on an
        // instance via xsi:type and have no xs:element statically bound to them, so the
        // type's own name must also be registered as a lookup key in its own right. Since
        // this key is literally the type's own name, it unambiguously describes that type.
        addCandidate(candidatesByKey, typeName, byLang, true);
        elementsByType
            .getOrDefault(typeName, List.of())
            .forEach(elementName -> addCandidate(candidatesByKey, elementName, byLang, false));
      });
    }

    candidatesByKey.forEach((key, candidates) ->
        resolve(key, candidates)
            .ifPresent(byLang -> {
              byLang.forEach((lang, description) ->
                  descriptions.add(new DescriptionKey(key, description, lang)));
              glossaryEntries.add(
                  new GlossaryEntry(
                      key, byLang.getOrDefault("de", ""), byLang.getOrDefault("en", "")));
            }));
    glossaryEntries.sort(Comparator.comparing(GlossaryEntry::key));
  }

  private static void addCandidate(
      Map<String, List<Candidate>> candidatesByKey,
      String key,
      Map<String, String> byLang,
      boolean authoritative) {
    candidatesByKey
        .computeIfAbsent(key, _ -> new ArrayList<>())
        .add(new Candidate(byLang, authoritative));
  }

  /**
   * Resolves all candidates found for one key to its description texts by language, or to none if
   * the key is ambiguous:
   *
   * <ul>
   *   <li>if the key is the name of a documented type, only that type's own definitions are
   *       considered and element aliases are ignored, since the key unambiguously refers to that
   *       exact type;
   *   <li>otherwise, all element aliases are considered;
   *   <li>if all considered candidates have the same texts (in every language), those are used; if
   *       they differ, distinct types happen to share this key, so nothing is emitted for it rather
   *       than silently keeping one of several possibly wrong descriptions.
   * </ul>
   *
   * <p>German and English texts are compared together, so a key never combines the German text of
   * one type with the English text of another. The result only depends on which candidates exist,
   * not on the order in which they were found.
   */
  private static Optional<Map<String, String>> resolve(String key, List<Candidate> candidates) {
    var authoritative = candidates.stream().anyMatch(Candidate::authoritative);
    var distinctTexts =
        candidates.stream()
            .filter(candidate -> candidate.authoritative() == authoritative)
            .map(Candidate::byLang)
            .distinct()
            .toList();

    if (distinctTexts.size() == 1) {
      return Optional.of(distinctTexts.getFirst());
    }

    log.warn(
        "ambiguous XSD description for key {}: {} differently documented {} share this key;"
            + " omitting it from the generated descriptions and the glossary",
        key,
        distinctTexts.size(),
        authoritative ? "type definitions" : "element aliases");
    return Optional.empty();
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
        var localName = element.getAttribute("name");
        var type = element.getAttribute("type");
        var name =
            !localName.contains(":") && type.contains(":")
                ? String.join(":", type.substring(0, type.indexOf(":")), localName)
                : localName;
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
      Document document, Resource currentResource, Map<String, Document> documents) {
    extractNamespacePrefixes(document);

    try {
      var expression = xPath.compile("//*[local-name()='import']");
      var imports = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(imports, currentResource, documents);

      expression = xPath.compile("//*[local-name()='include']");
      var includes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(includes, currentResource, documents);

      expression = xPath.compile("//*[local-name()='redefine']");
      var redefines = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      getXSDInChildren(redefines, currentResource, documents);
    } catch (XPathExpressionException e) {
      log.error("error by getting related xsd", e);
    }
  }

  /**
   * Resolves each {@code schemaLocation} relative to the resource that references it (not to a
   * single fixed prefix), so that files inside a subdirectory (e.g. {@code schema/ris/ris.xsd})
   * correctly resolve their own relative/bare {@code schemaLocation} siblings within that same
   * subdirectory instead of accidentally falling back to the top-level schema directory.
   */
  private void getXSDInChildren(
      NodeList nodeList, Resource currentResource, Map<String, Document> documents) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      resolveAndParseSchemaLocation((Element) nodeList.item(i), currentResource, documents);
    }
  }

  private void resolveAndParseSchemaLocation(
      Element importElement, Resource currentResource, Map<String, Document> documents) {
    var schemaLocationAttr = importElement.getAttribute("schemaLocation");
    if (schemaLocationAttr.isBlank()) {
      return;
    }

    Resource resource;
    try {
      resource = currentResource.createRelative(schemaLocationAttr);
    } catch (IOException e) {
      log.error("error resolving relative schema location: {}", schemaLocationAttr, e);
      return;
    }

    var key = resource.getDescription();
    if (documents.containsKey(key)) {
      return;
    }

    var document = getDocumentForLocation(resource);
    if (document == null) {
      return;
    }

    documents.put(key, document);
    getRelatedXSD(document, resource, documents);
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
        .filter(descriptionKey ->
            descriptionKey.key().equals(key) && descriptionKey.lang().equals(language))
        .findFirst()
        .map(DescriptionKey::description);
  }
}
