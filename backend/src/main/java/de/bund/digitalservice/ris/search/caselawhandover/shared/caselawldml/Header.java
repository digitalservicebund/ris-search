package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Header {
  @XmlElement(name = "p", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<HeaderParagraph> paragraphs;

  public JaxbHtml findShortTitle() {
    if (paragraphs == null) return null;
    return paragraphs.stream()
        .map(HeaderParagraph::getShortTitle)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
