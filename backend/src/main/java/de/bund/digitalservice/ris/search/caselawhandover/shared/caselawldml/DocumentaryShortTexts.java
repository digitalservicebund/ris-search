package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentaryShortTexts {

  @XmlElement(name = "titelzeile", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private JaxbHtml titleLine;
}
