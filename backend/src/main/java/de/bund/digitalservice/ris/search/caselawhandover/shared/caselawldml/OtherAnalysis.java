package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtherAnalysis {

  @XmlElement(name = "dokumentarischeKurztexte", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private DocumentaryShortTexts documentaryShortTexts;
}
