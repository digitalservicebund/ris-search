package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a specific provision or individual norm (Einzelnorm) within a legal statute. */
@Getter
@Setter
public class Einzelnorm {
  @XmlElement(name = "bezeichnung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String designation;
}
