package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FrbrAlias {
  @XmlAttribute private String name;
  @XmlAttribute private String value;

  public FrbrAlias(String value) {
    this.value = value;
  }
}
