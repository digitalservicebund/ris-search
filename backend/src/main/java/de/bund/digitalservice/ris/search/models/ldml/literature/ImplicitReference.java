package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;

@Getter
public class ImplicitReference {
  public enum Type {
    INDEPENDENT_REFERENCE,
    DEPENDENT_REFERENCE,
    NORM_REFERENCE
  }

  @XmlTransient private Type type;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "fundstelleUnselbstaendig", namespace = LiteratureNamespaces.RIS_NS)
  public void setFundstelleUnselbstaendig(Object ignore) {
    this.type = Type.DEPENDENT_REFERENCE;
  }

  @XmlElement(name = "fundstelleSelbstaendig", namespace = LiteratureNamespaces.RIS_NS)
  public void setFundstelleSelbstaendig(Object ignore) {
    this.type = Type.INDEPENDENT_REFERENCE;
  }

  @XmlElement(name = "normReference", namespace = LiteratureNamespaces.RIS_NS)
  public void setNormReference(Object ignore) {
    this.type = Type.NORM_REFERENCE;
  }
}
