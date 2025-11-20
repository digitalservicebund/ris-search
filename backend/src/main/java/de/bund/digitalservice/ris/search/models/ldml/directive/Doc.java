package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Doc {

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Meta meta;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Preface preface;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private MainBody mainBody;
}
