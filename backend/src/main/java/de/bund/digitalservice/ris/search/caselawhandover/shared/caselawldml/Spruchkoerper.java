package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class Spruchkoerper {

  @XmlAttribute private String domainTerm;

  @XmlAttribute(name = "refersTo", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private String refersTo;

  @XmlValue private String value;
}
