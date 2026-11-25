package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to another (Rechtsprechung) document. */
@Getter
@Setter
public class ReferenzRechtsprechung implements RisZitierung {

  @XmlElement(name = "richtung", namespace = Namespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = Namespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = Namespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = Namespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "gericht", namespace = Namespaces.RIS_NS)
  private RisGericht gericht;

  @XmlElement(name = "entscheidungsdatum", namespace = Namespaces.RIS_NS)
  private String entscheidungsdatum;

  @XmlElement(name = "dokumenttyp", namespace = Namespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "aktenzeichen", namespace = Namespaces.RIS_NS)
  private String aktenzeichen;

  @XmlElement(name = "fundstelle", namespace = Namespaces.RIS_NS)
  private Fundstelle fundstelle;
}
