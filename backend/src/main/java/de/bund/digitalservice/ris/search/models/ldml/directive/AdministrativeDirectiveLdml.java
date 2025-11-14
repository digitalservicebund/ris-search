package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "akomaNtoso", namespace = AdministrativeDirectiveLdml.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class AdministrativeDirectiveLdml {
  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_NS = "http://ldml.neuris.de/meta/";

  @XmlElement(name = "doc", namespace = AKN_NS)
  private Doc doc;
}
