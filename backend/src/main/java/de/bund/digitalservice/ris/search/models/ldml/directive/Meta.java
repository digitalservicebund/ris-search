package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Proprietary proprietary;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Analysis analysis;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Identification identification;

  @XmlElement(name = "classification", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<Classification> classifications;
}
