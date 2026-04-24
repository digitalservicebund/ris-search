package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the header section of a legal document, containing a list of paragraphs. */
@Getter
@NoArgsConstructor
public class Header {
  @XmlElement(name = "p", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<HeaderParagraph> paragraphs;

  /**
   * Searches through the header paragraphs to find and return the first available short title.
   *
   * @return the first {@link JaxbHtml} short title found, or {@code null} if none exists
   */
  public JaxbHtml findShortTitle() {
    if (paragraphs == null) {
      return null;
    }
    return paragraphs.stream()
        .map(HeaderParagraph::getShortTitle)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
