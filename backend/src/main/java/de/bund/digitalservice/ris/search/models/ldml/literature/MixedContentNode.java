package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAnyElement;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

public class MixedContentNode {
  @XmlAnyElement private List<Node> nodes;

  public String getNormalizedTextContent() {
    return nodes.stream()
        .map(Node::getTextContent)
        .map(MixedContentNode::normalize)
        .collect(Collectors.joining(StringUtils.EMPTY));
  }

  private static String normalize(String text) {
    return text.replace("\n", StringUtils.EMPTY).replaceAll("\\s+", StringUtils.SPACE).strip();
  }
}
