package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlMixed;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

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
    return text.replace("\n", StringUtils.SPACE).replaceAll("\\s+", StringUtils.SPACE).strip();
  }
}
