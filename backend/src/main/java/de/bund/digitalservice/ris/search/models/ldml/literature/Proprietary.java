package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Proprietary {

  @XmlElement(name = "meta", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private RisMeta meta;
}
