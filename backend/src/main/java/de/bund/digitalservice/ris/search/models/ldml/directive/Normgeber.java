package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Normgeber {
  @XmlAttribute private String staat;

  @XmlAttribute private String organ;
}
