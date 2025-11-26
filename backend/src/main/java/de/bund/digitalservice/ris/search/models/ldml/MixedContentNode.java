package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlMixed;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

/**
 * Represents a node containing mixed content in an XML-based data structure.
 *
 * <p>The `MixedContentNode` class allows storage and management of mixed content, which includes
 * both textual and non-textual nodes. This class is particularly useful when processing XML data
 * that contains heterogeneous content blocks, such as a mix of plain text and structured XML
 * elements.
 *
 * <p>Key Features: - The `nodes` field stores the mixed content, which can include strings
 * (representing text) and objects (representing XML elements) as part of the data model. - The
 * `getNormalizedTextContent` method provides a utility to retrieve the concatenated textual content
 * of the mixed nodes, normalized for consistent whitespace usage. - Whitespace normalization
 * ensures that sequences of whitespace characters are replaced with single spaces, and leading or
 * trailing whitespace is stripped.
 *
 * <p>This class leverages JAXB annotations for handling XML serialization and deserialization. The
 * `@XmlMixed` annotation allows for the inclusion of both text and nodes in the `nodes` field. The
 * `@XmlAnyElement` annotation enables dynamic handling of XML elements that might not match
 * predefined JAXB mappings.
 */
public class MixedContentNode {
  @XmlMixed @XmlAnyElement private List<Object> nodes;

  public String getNormalizedTextContent() {
    return normalize(
        nodes.stream()
            .map(
                obj ->
                    switch (obj) {
                      case String text -> text;
                      case Node node -> node.getTextContent();
                      default -> StringUtils.EMPTY;
                    })
            .collect(Collectors.joining(StringUtils.EMPTY)));
  }

  private static String normalize(String text) {
    return text.replaceAll("\\s+", StringUtils.SPACE).strip();
  }
}
