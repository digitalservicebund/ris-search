package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class Meta {

  @XmlElement(name = "identification", namespace = LiteratureNamespaces.AKN_NS)
  private Identification identification;

  @XmlElement(name = "proprietary", namespace = LiteratureNamespaces.AKN_NS)
  private Proprietary proprietary;

  @XmlElement(name = "classification", namespace = LiteratureNamespaces.AKN_NS)
  private List<Classification> classifications;

  @XmlElement(name = "analysis", namespace = LiteratureNamespaces.AKN_NS)
  private Analysis analysis;

  @XmlElement(name = "references", namespace = LiteratureNamespaces.AKN_NS)
  private References references;
}
