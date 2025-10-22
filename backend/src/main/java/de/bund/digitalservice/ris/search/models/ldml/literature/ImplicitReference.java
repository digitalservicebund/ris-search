package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class ImplicitReference {

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "fundstelleUnselbstaendig", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String fundstelleUnselbstaendig;

  @XmlElement(name = "fundstelleSelbstaendig", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String fundstelleSelbstaendig;

  @XmlElement(name = "normReference", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String normReference;
}
