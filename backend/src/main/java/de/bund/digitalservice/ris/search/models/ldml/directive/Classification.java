package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Classification {

  @XmlAttribute private String source;

  @XmlElement(name = "keyword", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<Keyword> keywords;
}
