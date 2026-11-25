package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** A citation of an administrative directive (ris:referenzVerwaltungsvorschrift). */
@Getter
public class ReferenzVerwaltungsvorschrift implements RisZitierung {

  @XmlElement(name = "richtung", namespace = LiteratureNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = LiteratureNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = LiteratureNamespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "jurisAbkuerzung", namespace = LiteratureNamespaces.RIS_NS)
  private String jurisAbkuerzung;

  @XmlElement(name = "fundstelle", namespace = LiteratureNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
