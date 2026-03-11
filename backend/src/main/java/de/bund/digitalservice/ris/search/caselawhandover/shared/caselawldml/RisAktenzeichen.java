package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Represents the file number (Aktenzeichen) for case law within an LDML document, including its
 * domain term and text value.
 */
public class RisAktenzeichen {
  @XmlAttribute(name = "domainTerm")
  private String domainTerm;

  @XmlValue private String value;

  public String getDomainTerm() {
    return domainTerm;
  }

  public String getValue() {
    return value != null ? value.trim() : null;
  }
}
