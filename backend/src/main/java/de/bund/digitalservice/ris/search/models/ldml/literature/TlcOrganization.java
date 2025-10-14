package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class TlcOrganization {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "name", namespace = LiteratureNamespaces.RIS_NS)
  private String name;
}
