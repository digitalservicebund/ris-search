package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to a piece of unselbstständige (dependent) literature. */
@Getter
@Setter
public class ReferenzUnselbststaendigeLiteratur implements RisZitierung {

  @XmlElement(name = "richtung", namespace = Namespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = Namespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = Namespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "autor", namespace = Namespaces.RIS_NS)
  private String autor;

  @XmlElement(name = "dokumenttyp", namespace = Namespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "fundstelle", namespace = Namespaces.RIS_NS)
  private Fundstelle fundstelle;
}
