package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to a piece of selbstständige (independent) literature. */
@Getter
@Setter
public class ReferenzSelbststaendigeLiteratur implements RisZitierung {

  @XmlElement(name = "richtung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "autor", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String autor;

  @XmlElement(name = "titel", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "veroeffentlichungsjahr", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String veroeffentlichungsjahr;
}
