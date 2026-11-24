package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** A citation of selbstständige literature (ris:referenzSelbststaendigeLiteratur). */
@Getter
public class ReferenzSelbststaendigeLiteratur implements RisZitierung {

  @XmlElement(name = "richtung", namespace = LiteratureNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = LiteratureNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "autor", namespace = LiteratureNamespaces.RIS_NS)
  private String autor;

  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "veroeffentlichungsjahr", namespace = LiteratureNamespaces.RIS_NS)
  private String veroeffentlichungsjahr;
}
