package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** Represents a person listed in the proprietary RIS metadata. */
@Getter
public class RisPerson {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlElement(name = "name", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private String name;

  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private String titel;
}
