package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class Court {

  @XmlAttribute private String domainTerm;

  @XmlAttribute(name = "refersTo", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private String refersTo;

  @XmlElement(name = "gerichtstyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String gerichtstyp;

  @XmlElement(name = "gerichtsort", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String gerichtsort;

  @XmlElement(name = "spruchkoerper", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Spruchkoerper spruchkoerper;
}
