package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Norm {
  @XmlElement(name = "abkuerzung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String abbreviation;

  @XmlElement(name = "einzelnorm", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<Einzelnorm> singleNorms;

  @XmlElement(name = "titel", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String title;
}
