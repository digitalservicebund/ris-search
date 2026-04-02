package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an implicit reference within the context of a case law, providing details of how the
 * reference is displayed and linked to norm and caselaw data.
 */
@Getter
@Setter
public class ImplicitReference {
  @XmlAttribute(name = "domainTerm")
  private String domainTerm;

  @XmlElement(name = "vorgehend", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private LinkedJudgement precedingJudgement;

  @XmlElement(name = "nachgehend", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private LinkedJudgement ensuingJudgement;
}
