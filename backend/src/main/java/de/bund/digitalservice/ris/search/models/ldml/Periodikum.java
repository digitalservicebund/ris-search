package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents the periodical (Periodikum) a Fundstelle was published in. */
@Getter
@Setter
public class Periodikum {

  @XmlElement(name = "abkuerzung", namespace = Namespaces.RIS_NS)
  private String abkuerzung;

  @XmlElement(name = "periodikumTyp", namespace = Namespaces.RIS_NS)
  private String periodikumTyp;

  @XmlElement(name = "titel", namespace = Namespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "untertitel", namespace = Namespaces.RIS_NS)
  private String untertitel;
}
