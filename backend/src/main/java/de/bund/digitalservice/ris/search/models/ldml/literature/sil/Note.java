package de.bund.digitalservice.ris.search.models.ldml.literature.sil;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Note {

  @XmlElement(namespace = LiteratureNamespaces.AKN_NS)
  Block block;
}
