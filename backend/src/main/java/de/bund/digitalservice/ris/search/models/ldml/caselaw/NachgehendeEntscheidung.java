package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * A subsequent decision that names the documented decision as its prior decision
 * (ris:nachgehendeEntscheidung).
 */
@Getter
@Setter
public class NachgehendeEntscheidung implements VerknuepfteEntscheidung {

  /** Either "nachgehend" (concluded) or "anhängig" (pending proceeding). */
  @XmlAttribute(name = "art")
  private String art;

  @XmlElement(name = "referenzURI", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String referenzUri;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumenttyp;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String entscheidungsdatum;

  @XmlElement(name = "mitteilungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String mitteilungsdatum;

  @XmlElement(name = "dokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String dokumentnummer;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String aktenzeichen;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht gericht;

  @XmlElement(name = "vermerk", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String vermerk;

  @Override
  public boolean isAnhaengig() {
    return "anhängig".equalsIgnoreCase(art);
  }
}
