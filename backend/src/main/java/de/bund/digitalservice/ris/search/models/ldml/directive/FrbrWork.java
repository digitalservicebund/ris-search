package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrbrWork {

  @XmlElement(name = "FRBRalias", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<FrbrNameValueElement> frbrAliasList;
}
