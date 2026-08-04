package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a person referenced in the RIS metadata of a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class RisPerson {

  @XmlAttribute(name = "domainTerm")
  private String domainTerm;

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlElement(name = "name", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String name;
}
