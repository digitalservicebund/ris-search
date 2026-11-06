package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** Represents an organisation listed in the proprietary RIS metadata. */
@Getter
public class RisOrganisation {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "name", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String name;

  @XmlElement(name = "ort", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String ort;

  @XmlElement(name = "zusatz", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String zusatz;

  @XmlElement(name = "jahr", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String jahr;
}
