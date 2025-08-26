package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Proprietary {

  @XmlElement(name = "metadata", namespace = LiteratureLdml.RIS_NS)
  private Metadata metadata;
}
