package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Meta {
  @XmlElement(name = "identification", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Identification identification;

  @XmlElement(name = "classification", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Classification classification;

  @XmlElement(name = "proprietary", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private Proprietary proprietary;
}
