package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrbrDate {
  @XmlAttribute() private LocalDate date;

  @XmlAttribute() private String name;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String domainTerm;
}
