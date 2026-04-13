package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AknKeyword {

  @XmlAttribute(name = "domainTerm", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String domainTerm;

  @XmlAttribute(name = "dictionary")
  private String dictionary;

  @XmlAttribute(name = "showAs")
  private String showAs;

  @XmlAttribute(name = "value")
  private String value;
}
