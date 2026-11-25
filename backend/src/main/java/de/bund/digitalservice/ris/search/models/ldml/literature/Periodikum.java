package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** The periodical (ris:periodikum) a Fundstelle of a cited document was published in. */
@Getter
public class Periodikum {

  @XmlElement(name = "abkuerzung", namespace = LiteratureNamespaces.RIS_NS)
  private String abkuerzung;

  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_NS)
  private String titel;
}
