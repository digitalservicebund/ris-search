package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents a person or institution ({@code ris:personInstitution}) listed in the proprietary RIS
 * metadata.
 */
@Getter
public class RisPersonInstitution {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "name", namespace = LiteratureNamespaces.RIS_NS)
  private String name;

  @XmlElement(name = "zusatz", namespace = LiteratureNamespaces.RIS_NS)
  private String zusatz;
}
