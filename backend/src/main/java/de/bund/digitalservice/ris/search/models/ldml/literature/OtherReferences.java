package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class OtherReferences {

  @XmlAttribute(name = "source")
  private String source;

  @XmlElement(name = "implicitReference", namespace = LiteratureNamespaces.AKN_NS)
  private List<ImplicitReference> implicitReferences;
}
