package de.bund.digitalservice.ris.search.xsd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Slf4j
public class DocumentationElement {
  @Getter
  private final XSDElement parent;

  @Getter
  private final String documentation;

  @Getter
  private final String language;

  public DocumentationElement(Element element, String targetNamespaceURI) {
    this.parent = getParent(element, targetNamespaceURI);
    this.documentation = preprocessContent(element.getTextContent().trim());
    var languageAttribute = element.getAttribute("xml:lang");
    if (languageAttribute.isBlank()) {
      languageAttribute = "de";
    }
    this.language = languageAttribute;
  }

  private String preprocessContent(String description) {
    StringBuilder stringBuilder = new StringBuilder();
    var lines = description.split("\n");
    var listOpen = false;
    for (String line : lines) {
      if (line.trim().startsWith("*")) {
        if (!listOpen) {
          stringBuilder.append("<ul>");
          listOpen = true;
        }
        stringBuilder.append("<li>").append(line.trim().substring(1).trim()).append("</li>");
      } else {
        if (listOpen) {
          stringBuilder.append("</ul>");
        }
        listOpen = false;
        stringBuilder.append(line.trim()).append("<br>");
      }
    }
    return stringBuilder.toString();
  }

  private XSDElement getParent(Element element, String targetNamespaceURI) {
    Node parentNode = element.getParentNode();

    if (parentNode == null) {
      return null;
    }

    if (parentNode instanceof Element parentElement) {
      if (parentElement.getTagName().equals("xs:simpleType") || parentElement.getTagName().equals("xs:complexType")) {
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
