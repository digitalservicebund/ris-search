package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a legal citation, consisting of a periodical and a specific position within it. */
@Getter
@Setter
public class Citation {
  @XmlElement(name = "periodikum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Periodical periodical;

  @XmlElement(name = "zitatstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String citationPosition;
}
