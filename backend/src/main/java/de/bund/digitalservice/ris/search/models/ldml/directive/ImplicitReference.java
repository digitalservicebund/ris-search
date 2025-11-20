package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImplicitReference {

  @XmlAttribute private String showAs;

  @XmlAttribute private String shortForm;

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private NormReference normReference;

  @XmlElement(namespace = AdministrativeDirectiveLdml.RIS_NS)
  private CaselawReference caselawReference;
}
