package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Note {

  @XmlElement(namespace = LiteratureNamespaces.AKN_NS)
  Block block;
}
