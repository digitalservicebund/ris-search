package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Proprietary {

  @XmlElement(name = "meta", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private RisMeta meta;
}
