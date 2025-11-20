package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Zuordnung {
  @XmlAttribute private String aspekt;

  @XmlAttribute private String begriff;
}
