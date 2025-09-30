package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

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
public class RelatedDecision {
  @XmlAttribute private String date;

  @XmlElement(name = "documentNumber", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentNumber;

  @XmlElement(name = "fileNumber", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fileNumber;

  @XmlElement(name = "courtType", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String courtType;
}
