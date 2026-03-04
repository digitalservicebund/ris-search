package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Periodical {
  @XmlElement(name = "abkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String abbreviation;

  @XmlElement(name = "periodikumTyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String type; // official vs non-official
}
