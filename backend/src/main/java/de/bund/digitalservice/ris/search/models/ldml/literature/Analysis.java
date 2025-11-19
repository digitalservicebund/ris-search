package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class Analysis {

  @XmlElement(name = "otherReferences", namespace = LiteratureNamespaces.AKN_NS)
  private List<OtherReferences> otherReferences;
}
