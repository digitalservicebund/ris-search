package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Analysis {

  @XmlElement(name = "otherReferences", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private OtherReferences otherReferences;
}
