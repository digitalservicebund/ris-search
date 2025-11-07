package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaselawReference {

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String court;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private LocalDate date;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String referenceNumber;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String documentNumber;

  @XmlAttribute(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private String courtLocation;
}
