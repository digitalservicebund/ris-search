package de.bund.digitalservice.ris.search.xsd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Reads the {@code xs:documentation} of the types in the {@code ris} namespace from the XSD schema,
 * so that field descriptions don't have to be duplicated in Java code.
 *
 * <p>All work is done once, in the constructor:
 *
 * <ol>
 *   <li>Load {@code ris.xsd} and every schema file it imports, includes or redefines.
 *   <li>Find the {@code xs:documentation} texts that belong to named types of the {@code ris}
 *       namespace. Documentation of elements, attributes and types of other namespaces (e.g. Akoma
 *       Ntoso) is ignored.
 *   <li>Group these texts into one {@link TypeDocumentation} per type, with its German and English
 *       text.
 * </ol>
 *
 * <p>Afterwards, {@link #findDescription} looks up the description of a type, e.g. for the OpenAPI
 * descriptions of {@link RISSchema} fields, and {@link #getTypeDocumentation} returns the
 * documentation of all types, e.g. for the glossary.
 *
 * <p>The parser runs at backend startup as well as in the glossary generator. Problems with the
 * schema files are therefore logged instead of thrown, so that the backend can still start.
 */
public class XSDDescriptionParser {
  private record SchemaFile(String fileName, Document document) {}

  private record DocumentationText(
      String typeName, String language, String text, String schemaFile) {}

  private static final Logger logger = LogManager.getLogger(XSDDescriptionParser.class);
  private static final String DEFAULT_RIS_PREFIX = "ris";

  private final DocumentBuilderFactory factory = createDocumentBuilderFactory();

  /** The documentation per type name, sorted by type name; empty if the schema can't be read. */
  private Map<String, TypeDocumentation> documentationPerType = Map.of();

  /**
   * Reads the schema at {@code xsd.ris-namespace-location} in three steps:
   *
   * <ol>
   *   <li>{@link #loadSchemaFiles}: load {@code ris.xsd} and every schema file it imports, includes
   *       or redefines.
   *   <li>{@link #findRisTypeDocumentation}: find the {@code xs:documentation} texts that belong to
   *       types of the {@code ris} namespace.
   *   <li>{@link #groupByType}: group these texts into one {@link TypeDocumentation} per type.
   * </ol>
   *
   * <p>If the location is not set or the schema can't be loaded, no descriptions are available.
   */
  public XSDDescriptionParser(XSDDescriptionProperties properties, ResourceLoader resourceLoader) {
    var location = properties.getRisNamespaceLocation();
    if (location == null || location.isBlank()) {
      logger.error("xsd.ris-namespace-location is not set; no XSD descriptions will be available");
      return;
    }

    // Step 1: load ris.xsd and every schema file it imports, includes or redefines
    var rootResource = resourceLoader.getResource(location);
    var rootSchema = parse(rootResource);
    if (rootSchema.isEmpty()) {
      logger.error("could not load XSD schema {}; no XSD descriptions will be available", location);
      return;
    }
    var schemaFiles = loadSchemaFiles(rootResource, rootSchema.get());

    // Step 2: find xs:documentation texts of the ris types
    var risTypeDocumentation = findRisTypeDocumentation(rootSchema.get(), schemaFiles);

    // Step 3: group these texts into one TypeDocumentation per type
    documentationPerType = groupByType(risTypeDocumentation);
  }

  /**
   * Looks up the parsed XSD description for the given type name and language.
   *
   * @param key the XSD type name (e.g. "ris:dokumentnummer") to look up
   * @param language the language of the documentation text to match (e.g. "de")
   * @return the description text, if one was parsed from the XSD for that key and language
   */
  public Optional<String> findDescription(String key, String language) {
    return Optional.ofNullable(documentationPerType.get(key))
        .map(documentation -> documentation.documentationIn(language))
        .filter(text -> !text.isEmpty());
  }

  /**
   * Returns the documentation of all types of the {@code ris} namespace.
   *
   * @return one entry per type, sorted by type name
   */
  public List<TypeDocumentation> getTypeDocumentation() {
    return List.copyOf(documentationPerType.values());
  }

  /**
   * Step 1: loads, transitively, every schema file the already parsed root schema references via
   * {@code xs:import}, {@code xs:include} or {@code xs:redefine}. Each file is loaded only once.
   * Referenced files that don't exist are skipped; files that can't be resolved or parsed are
   * skipped with a logged error.
   *
   * @return the root schema and all loaded files, in no particular order
   */
  private List<SchemaFile> loadSchemaFiles(Resource rootResource, Document rootSchema) {
    List<SchemaFile> result = new ArrayList<>();
    result.add(new SchemaFile(rootResource.getFilename(), rootSchema));
    Set<String> processed = new HashSet<>(Set.of(rootResource.getDescription()));
    List<Resource> toProcess = new ArrayList<>(findReferencedSchemas(rootSchema, rootResource));

    while (!toProcess.isEmpty()) {
      var resource = toProcess.removeLast();
      if (!processed.add(resource.getDescription())) {
        continue;
      }
      parse(resource)
          .ifPresent(document -> {
            result.add(new SchemaFile(resource.getFilename(), document));
            toProcess.addAll(findReferencedSchemas(document, resource));
          });
    }

    return result;
  }

  private Optional<Document> parse(Resource resource) {
    if (!resource.exists()) {
      return Optional.empty();
    }
    try (InputStream inputStream = resource.getInputStream()) {
      return Optional.of(factory.newDocumentBuilder().parse(inputStream));
    } catch (IOException | SAXException | ParserConfigurationException e) {
      logger.error("could not parse XSD schema {}", resource, e);
      return Optional.empty();
    }
  }

  /**
   * Finds the schema files referenced by {@code xs:import}, {@code xs:include} and {@code
   * xs:redefine}. Each {@code schemaLocation} is resolved relative to the file that references it:
   * in {@code ris/ris.xsd}, {@code caselaw-decision.xsd} becomes {@code ris/caselaw-decision.xsd}
   * and {@code ../akn/akomantoso30.xsd} becomes {@code akn/akomantoso30.xsd}.
   */
  private static List<Resource> findReferencedSchemas(Document document, Resource resource) {
    return Stream.of("import", "include", "redefine")
        .flatMap(localName -> findSchemaElements(document, localName).stream())
        .map(reference -> reference.getAttribute("schemaLocation"))
        .filter(schemaLocation -> !schemaLocation.isBlank())
        .flatMap(schemaLocation -> resolveRelative(resource, schemaLocation).stream())
        .toList();
  }

  private static Optional<Resource> resolveRelative(Resource resource, String schemaLocation) {
    try {
      return Optional.of(resource.createRelative(schemaLocation));
    } catch (IOException e) {
      logger.error("could not resolve schema location {} in {}", schemaLocation, resource, e);
      return Optional.empty();
    }
  }

  /**
   * Step 2: finds the {@code xs:documentation} texts that belong to a named type of the {@code ris}
   * namespace, i.e. the target namespace of the root schema. Schema files of other namespaces (e.g.
   * Akoma Ntoso) are skipped, as is documentation of elements, attributes and anonymous types.
   *
   * @return one entry per documentation text, with the name of its type (e.g. {@code
   *     ris:tatbestand}), its language and the schema file it is in
   */
  private static List<DocumentationText> findRisTypeDocumentation(
      Document rootSchema, List<SchemaFile> schemaFiles) {
    var rootElement = rootSchema.getDocumentElement();
    var risNamespaceUri = rootElement.getAttribute("targetNamespace");
    var risPrefix =
        Objects.requireNonNullElse(rootElement.lookupPrefix(risNamespaceUri), DEFAULT_RIS_PREFIX);

    return schemaFiles.stream()
        .filter(schemaFile -> risNamespaceUri.equals(targetNamespaceOf(schemaFile)))
        .flatMap(schemaFile -> findTypeDocumentation(schemaFile, risPrefix))
        .toList();
  }

  /**
   * Finds the {@code xs:documentation} texts in one schema file that belong to a named type.
   * Documentation of elements, attributes and anonymous types (without a {@code name}) is skipped.
   */
  private static Stream<DocumentationText> findTypeDocumentation(
      SchemaFile schemaFile, String prefix) {
    return findSchemaElements(schemaFile.document(), "documentation").stream()
        .map(DocumentationElement::new)
        .filter(documentation -> documentation.getTypeName() != null)
        .map(documentation ->
            new DocumentationText(
                prefix + ":" + documentation.getTypeName(),
                documentation.getLanguage(),
                documentation.getDocumentation(),
                schemaFile.fileName()));
  }

  /**
   * Step 3: groups the documentation texts by type name into one {@link TypeDocumentation} per
   * type, with its German and English text and the schema file it is defined in.
   *
   * @return the documentation per type name, sorted by type name
   */
  private static Map<String, TypeDocumentation> groupByType(List<DocumentationText> texts) {
    return texts.stream()
        .collect(
            Collectors.groupingBy(
                DocumentationText::typeName,
                TreeMap::new,
                Collectors.collectingAndThen(
                    Collectors.toList(), XSDDescriptionParser::toTypeDocumentation)));
  }

  /** Combines the documentation texts of one type into its {@link TypeDocumentation}. */
  private static TypeDocumentation toTypeDocumentation(List<DocumentationText> textsOfOneType) {
    var first = textsOfOneType.getFirst();
    return new TypeDocumentation(
        first.typeName(),
        textIn(textsOfOneType, "de"),
        textIn(textsOfOneType, "en"),
        first.schemaFile());
  }

  /** Returns the text in the given language, or blank if there is none. */
  private static String textIn(List<DocumentationText> texts, String language) {
    return texts.stream()
        .filter(text -> text.language().equals(language))
        .map(DocumentationText::text)
        .findFirst()
        .orElse("");
  }

  private static String targetNamespaceOf(SchemaFile schemaFile) {
    return schemaFile.document().getDocumentElement().getAttribute("targetNamespace");
  }

  /**
   * Finds all XML Schema elements with the given local name (e.g. {@code documentation}),
   * regardless of the prefix the file uses for the XML Schema namespace ({@code xs:} or {@code
   * xsd:}).
   */
  private static List<Element> findSchemaElements(Document document, String localName) {
    var nodes = document.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, localName);
    return IntStream.range(0, nodes.getLength()).mapToObj(i -> (Element) nodes.item(i)).toList();
  }

  /**
   * Creates a namespace-aware {@link DocumentBuilderFactory} that is protected against XML external
   * entity (XXE) attacks: the schema files must not load DTDs or external entities.
   *
   * @throws IllegalStateException if the XML parser does not support this protection
   */
  private static DocumentBuilderFactory createDocumentBuilderFactory() {
    var factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    try {
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("XML parser does not support XXE protection", e);
    }
    return factory;
  }
}
