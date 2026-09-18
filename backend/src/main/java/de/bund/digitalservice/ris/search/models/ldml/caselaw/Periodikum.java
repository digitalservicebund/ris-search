package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents the periodical (Periodikum) a Fundstelle was published in. */
@Getter
@Setter
public class Periodikum {

  @XmlElement(name = "abkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String abkuerzung;

  @XmlElement(name = "periodikumTyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String periodikumTyp;

  @XmlElement(name = "titel", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "untertitel", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String untertitel;
}
