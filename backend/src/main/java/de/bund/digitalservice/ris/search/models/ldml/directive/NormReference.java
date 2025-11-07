package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormReference {

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private LocalDate dateOfRelevance;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private LocalDate dateOfVersion;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String singleNorm;
}
