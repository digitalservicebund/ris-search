package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the judicial body (Spruchkörper) within a court.
 *
 * <p>In the German legal system, this refers to the specific chamber, senate, or panel responsible
 * for the decision. This class maps the body using both domain-specific terms and Akoma Ntoso
 * references.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class Spruchkoerper {

  /** The domain-specific identifier for the judicial body. */
  @XmlAttribute private String domainTerm;

  /** A reference to a formal definition within the Akoma Ntoso namespace. */
  @XmlAttribute(name = "refersTo", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private String refersTo;

  /** The actual name or designation of the judicial body (e.g., "1. Senat"). */
  @XmlValue private String value;
}
