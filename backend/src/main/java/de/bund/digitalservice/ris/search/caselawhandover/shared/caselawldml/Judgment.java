package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the Judgment element in a case law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Judgment {
  @Builder.Default
  @XmlAttribute(name = "name")
  private String name = "documentType test";

  @XmlElement(name = "meta", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Meta meta;

  @XmlElement(name = "header", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml header;

  @XmlElement(name = "judgmentBody", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JudgmentBody judgmentBody;
}
