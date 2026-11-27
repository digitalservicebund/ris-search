package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a single specific provision (Einzelnorm) of a referenced law. */
@Getter
@Setter
public class Einzelnorm {

  @XmlElement(name = "bezeichnung", namespace = Namespaces.RIS_NS)
  private String bezeichnung;

  @XmlElement(name = "gesetzeskraft", namespace = Namespaces.RIS_NS)
  private Gesetzeskraft gesetzeskraft;

  @XmlElement(name = "fassungsdatum", namespace = Namespaces.RIS_NS)
  private String fassungsdatum;

  @XmlElement(name = "jahr", namespace = Namespaces.RIS_NS)
  private String jahr;
}
