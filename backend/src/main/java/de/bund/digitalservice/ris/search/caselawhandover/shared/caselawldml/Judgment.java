package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the Judgment element in a case law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "judgment", namespace = CaseLawLdmlNamespaces.AKN_NS)
public class Judgment {

  @XmlAttribute(name = "name")
  private String name;

  @XmlElement(name = "meta", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Meta meta;

  @XmlElement(name = "header", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Header header;

  @XmlElement(name = "judgmentBody", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JudgmentBody judgmentBody;
}
