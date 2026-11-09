package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to a piece of unselbstständige (dependent) literature. */
@Getter
@Setter
public class ReferenzUnselbststaendigeLiteratur implements RisReferenz {

  @XmlElement(name = "richtung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "autor", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String autor;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "fundstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
