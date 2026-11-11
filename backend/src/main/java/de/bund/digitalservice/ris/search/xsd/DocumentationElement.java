package de.bund.digitalservice.ris.search.xsd;

import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import lombok.Getter;
import org.w3c.dom.Element;

/**
 * A parsed {@code xs:documentation} element: the description text and language it carries, plus the
 * name of the type it documents, if any, and the enumeration value it describes, if any.
 */
public class DocumentationElement {
  /**
   * The name of the named type this documentation belongs to (e.g. {@code tatbestand}), or {@code
   * null} if it documents something else, such as an element, an attribute or an anonymous type.
   */
  @Getter private final String typeName;

  /**
   * The value this documentation describes if it belongs to an {@code xs:enumeration} of the type
   * (e.g. {@code aktiv}), or {@code null} if it describes the type itself.
   */
  @Getter private final String enumerationValue;

  @Getter private final String documentation;

  @Getter private final String language;

  /**
   * Parses the given {@code xs:documentation} DOM element, resolving the type it documents and
   * normalizing its language to {@code de} when unset.
   */
  public DocumentationElement(Element element) {
    this.typeName = findTypeName(element);
    this.enumerationValue = findEnumerationValue(element);
    this.documentation = normalizeLines(element.getTextContent());
    var languageAttribute = element.getAttribute("xml:lang");
    if (languageAttribute.isBlank()) {
      languageAttribute = "de";
    }
    this.language = languageAttribute;
  }

  /**
   * Trims each line of the raw XSD documentation text, removing the source file's indentation
   * without altering the line breaks or {@code *}-bullet markers themselves.
   */
  private static String normalizeLines(String text) {
    return text.trim().lines().map(String::trim).collect(Collectors.joining("\n"));
  }

  /**
   * Walks up from the {@code xs:documentation} element to the nearest type, element or attribute
   * declaration. Only a named type yields a name. An element or attribute stops the search, so that
   * their documentation is not mistaken for the documentation of the type that contains them.
   */
  private static String findTypeName(Element element) {
    if (!(element.getParentNode() instanceof Element parent)) {
      return null;
    }

    if (isSchemaElement(parent, "simpleType") || isSchemaElement(parent, "complexType")) {
      var name = parent.getAttribute("name");
      return name.isBlank() ? null : name;
    }
    if (isSchemaElement(parent, "element")
        || isSchemaElement(parent, "attribute")
        || isSchemaElement(parent, "attributeGroup")) {
      return null;
    }
    return findTypeName(parent);
  }

  /**
   * Returns the {@code value} of the {@code xs:enumeration} this documentation belongs to, or
   * {@code null} if it doesn't belong to one. Documentation always sits in an {@code xs:annotation}
   * directly inside the component it documents, so the enumeration is the parent of the annotation.
   */
  private static String findEnumerationValue(Element element) {
    if (element.getParentNode() instanceof Element annotation
        && annotation.getParentNode() instanceof Element owner
        && isSchemaElement(owner, "enumeration")) {
      return owner.getAttribute("value");
    }
    return null;
  }

  /**
   * Whether the given DOM element is the XML Schema element with the given local name, regardless
   * of the prefix the schema file uses for the XML Schema namespace (e.g. {@code xs:} or {@code
   * xsd:}).
   */
  private static boolean isSchemaElement(Element element, String localName) {
    return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(element.getNamespaceURI())
        && localName.equals(element.getLocalName());
  }
}
