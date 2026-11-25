package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** A citation of a court decision (ris:referenzRechtsprechung). */
@Getter
public class ReferenzRechtsprechung implements RisZitierung {

  @XmlElement(name = "richtung", namespace = LiteratureNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = LiteratureNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = LiteratureNamespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "gericht", namespace = LiteratureNamespaces.RIS_NS)
  private RisGericht gericht;

  @XmlElement(name = "entscheidungsdatum", namespace = LiteratureNamespaces.RIS_NS)
  private String entscheidungsdatum;

  @XmlElement(name = "dokumenttyp", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "aktenzeichen", namespace = LiteratureNamespaces.RIS_NS)
  private String aktenzeichen;

  @XmlElement(name = "fundstelle", namespace = LiteratureNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
