package de.bund.digitalservice.ris.search.xsd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A parsed {@code xs:documentation} element: the description text and language it carries, plus the
 * {@link XSDElement} (type or element) it documents.
 */
@Slf4j
public class DocumentationElement {
  @Getter private final XSDElement parent;

  @Getter private final String documentation;

  @Getter private final String language;

  /**
   * Parses the given {@code xs:documentation} DOM element, resolving its documented parent (type or
   * element) and normalizing its language to {@code de} when unset.
   */
  public DocumentationElement(Element element, String targetNamespaceURI) {
    this.parent = getParent(element, targetNamespaceURI);
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
    return text.trim().lines().map(String::trim).reduce((a, b) -> a + "\n" + b).orElse("");
  }

  private XSDElement getParent(Element element, String targetNamespaceURI) {
    Node parentNode = element.getParentNode();

    if (parentNode == null) {
      return null;
    }

    if (parentNode instanceof Element parentElement) {
      if (parentElement.getTagName().equals("xs:simpleType")
          || parentElement.getTagName().equals("xs:complexType")) {
        var name = parentElement.getAttribute("name");
        var namespaceURI = parentElement.getNamespaceURI();
        if (!name.contains(":")) {
          namespaceURI = targetNamespaceURI;
        }

        return new TypeElement(namespaceURI, name);
      } else if (parentElement.getTagName().equals("xs:element")) {
        var name = parentElement.getAttribute("name");
        if (name.isBlank()) {
          name = parentElement.getAttribute("ref");
        }
        return new ElementElement(parentElement.getNamespaceURI(), name);
      } else {
        return getParent(parentElement, targetNamespaceURI);
      }
    }

    return null;
  }
}
