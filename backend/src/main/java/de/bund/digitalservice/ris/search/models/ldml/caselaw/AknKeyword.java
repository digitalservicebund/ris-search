package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a keyword element within the Legal Document Markup Language (LDML) for case law
 * documents. *
 *
 * <p>This class maps to metadata used to categorize or tag legal documents, typically following the
 * Akoma Ntoso XML schema for legislative and judiciary documents.
 */
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
