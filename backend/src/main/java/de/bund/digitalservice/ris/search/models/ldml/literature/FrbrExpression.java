package de.bund.digitalservice.ris.search.models.ldml.literature;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrLanguage;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class FrbrExpression {

  @XmlElement(name = "FRBRlanguage", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrLanguage> frbrLanguages;
}
