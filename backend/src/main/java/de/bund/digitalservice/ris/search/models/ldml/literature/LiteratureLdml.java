package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@Getter
@XmlRootElement(name = "akomaNtoso", namespace = LiteratureLdml.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class LiteratureLdml {
  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_NS = "http://ldml.neuris.de/literature/metadata/";

  @XmlElement(name = "doc", namespace = LiteratureLdml.AKN_NS)
  private Doc doc;
}
