package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@Getter
@XmlRootElement(name = "akomaNtoso", namespace = LiteratureNamespaces.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class LiteratureLdml {

  @XmlElement(name = "doc", namespace = LiteratureNamespaces.AKN_NS)
  private Doc doc;
}
