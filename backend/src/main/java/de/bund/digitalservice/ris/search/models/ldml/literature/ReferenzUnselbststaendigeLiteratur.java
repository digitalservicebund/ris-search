package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** A citation of unselbstständige literature (ris:referenzUnselbststaendigeLiteratur). */
@Getter
public class ReferenzUnselbststaendigeLiteratur implements RisZitierung {

  @XmlElement(name = "richtung", namespace = LiteratureNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = LiteratureNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "autor", namespace = LiteratureNamespaces.RIS_NS)
  private String autor;

  @XmlElement(name = "dokumenttyp", namespace = LiteratureNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "fundstelle", namespace = LiteratureNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
