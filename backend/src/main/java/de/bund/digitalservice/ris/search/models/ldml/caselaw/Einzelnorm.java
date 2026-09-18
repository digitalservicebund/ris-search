package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a single specific provision (Einzelnorm) of a referenced law. */
@Getter
@Setter
public class Einzelnorm {

  @XmlElement(name = "bezeichnung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String bezeichnung;

  @XmlElement(name = "gesetzeskraft", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Gesetzeskraft gesetzeskraft;

  @XmlElement(name = "fassungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fassungsdatum;

  @XmlElement(name = "jahr", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String jahr;
}
