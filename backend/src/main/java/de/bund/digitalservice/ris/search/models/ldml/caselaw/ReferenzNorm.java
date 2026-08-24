package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Represents a reference to a norm (law), including its individually cited provisions. */
@Getter
@Setter
public class ReferenzNorm {

  @XmlElement(name = "abkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String abkuerzung;

  @XmlElement(name = "titel", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "einzelnorm", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<Einzelnorm> einzelnorm;
}
