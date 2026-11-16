package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * A prior decision in the same (or a related) proceeding of the documented decision
 * (ris:vorgehendeEntscheidung).
 */
@Getter
@Setter
public class VorgehendeEntscheidung implements VerknuepfteEntscheidung {

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String entscheidungsdatum;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String aktenzeichen;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht gericht;
}
