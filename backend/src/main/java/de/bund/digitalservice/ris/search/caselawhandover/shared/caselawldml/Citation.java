package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Citation {
  @XmlElement(name = "periodikum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Periodical periodical;

  @XmlElement(name = "zitatstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String citationPosition; // e.g., "138, 287-300"
}
