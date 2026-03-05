package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a header paragraph within the case law LDML structure. */
@Getter
@NoArgsConstructor
public class HeaderParagraph {
  @XmlElement(name = "shortTitle", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml shortTitle;
}
