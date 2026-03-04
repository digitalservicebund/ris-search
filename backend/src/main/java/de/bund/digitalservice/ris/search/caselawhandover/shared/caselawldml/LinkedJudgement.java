package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedJudgement {
  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentType;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String decisionDate;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fileNumber;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Court court;

  public String asString() {
    return String.join(", ", List.of(this.fileNumber, this.court.getGerichtstyp()));
  }
}
