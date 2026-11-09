package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to another case law (Rechtsprechung) document. */
@Getter
@Setter
public class ReferenzRechtsprechung implements RisReferenz {

  @XmlElement(name = "richtung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht gericht;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String entscheidungsdatum;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String aktenzeichen;

  @XmlElement(name = "fundstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
