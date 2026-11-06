package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** Represents a congress (Kongress) listed in the proprietary RIS metadata. */
@Getter
public class RisKongress {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "name", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String name;

  @XmlElement(name = "jahr", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String jahr;

  @XmlElement(name = "ort", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String ort;

  @XmlElement(name = "land", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String land;
}
