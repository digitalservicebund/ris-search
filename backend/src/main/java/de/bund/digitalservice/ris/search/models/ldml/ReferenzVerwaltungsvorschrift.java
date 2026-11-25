package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to an administrative directive (Verwaltungsvorschrift). */
@Getter
@Setter
public class ReferenzVerwaltungsvorschrift implements RisZitierung {

  @XmlElement(name = "richtung", namespace = Namespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = Namespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = Namespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = Namespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "jurisAbkuerzung", namespace = Namespaces.RIS_NS)
  private String jurisAbkuerzung;

  @XmlElement(name = "fundstelle", namespace = Namespaces.RIS_NS)
  private Fundstelle fundstelle;
}
