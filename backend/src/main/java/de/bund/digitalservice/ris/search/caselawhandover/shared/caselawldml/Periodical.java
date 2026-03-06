package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a legal periodical or publication where case law might be reported. */
@Getter
@Setter
public class Periodical {
  @XmlElement(name = "abkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String abbreviation;

  @XmlElement(name = "periodikumTyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String type;
}
