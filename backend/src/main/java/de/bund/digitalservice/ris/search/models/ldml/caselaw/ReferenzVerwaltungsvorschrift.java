package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to an administrative directive (Verwaltungsvorschrift). */
@Getter
@Setter
public class ReferenzVerwaltungsvorschrift implements RisReferenz {

  @XmlElement(name = "richtung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String richtung;

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "artDerZitierung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String artDerZitierung;

  @XmlElement(name = "jurisAbkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String jurisAbkuerzung;

  @XmlElement(name = "fundstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Fundstelle fundstelle;
}
