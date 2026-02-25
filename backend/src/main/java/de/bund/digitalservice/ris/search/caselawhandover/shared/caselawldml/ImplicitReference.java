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

  @XmlAttribute private String showAs;

  @XmlAttribute private String shortForm;

  @XmlElement(namespace = CaseLawLdmlNamespaces.RIS_NS)
  private NormReference normReference;

  @XmlElement(namespace = CaseLawLdmlNamespaces.RIS_NS)
  private CaselawReference caselawReference;
}
