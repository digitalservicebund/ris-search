package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Doc {
  @XmlElement(name = "meta", namespace = LiteratureNamespaces.AKN_NS)
  private Meta meta;

  @XmlElement(name = "mainBody", namespace = LiteratureNamespaces.AKN_NS)
  private MainBody mainBody;
}
