package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

@Getter
public class Metadata {
  @XmlElementWrapper(name = "veroeffentlichungsJahre", namespace = LiteratureNamespaces.RIS_NS)
  @XmlElement(name = "veroeffentlichungsJahr", namespace = LiteratureNamespaces.RIS_NS)
  private List<String> yearsOfPublication;
}
