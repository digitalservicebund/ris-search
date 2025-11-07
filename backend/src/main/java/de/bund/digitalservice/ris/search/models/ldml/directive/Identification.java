package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identification {

  @XmlElement(name = "FRBRWork", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private FrbrWork frbrWork;

  @XmlElement(name = "FRBRExpression", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private FrbrExpression frbrExpression;
}
