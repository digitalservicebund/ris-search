package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to an administrative directive (Verwaltungsvorschrift). */
@Getter
@Setter
public class ReferenzVerwaltungsvorschrift {

  @XmlElement(name = "richtung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;
}
