package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a court entity within the Case Law LDML structure. *
 *
 * <p>This class maps court-specific data, including organizational hierarchy and geographic
 * location, using both standard Akoma Ntoso and custom RIS namespaces.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class Court {

  /** The domain-specific term identifying the court. */
  @XmlAttribute private String domainTerm;

  /** Reference to a formal definition or IRI within the Akoma Ntoso namespace. */
  @XmlAttribute(name = "refersTo", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private String refersTo;

  /** The type of court (e.g., Amtsgericht, Landgericht). */
  @XmlElement(name = "gerichtstyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String gerichtstyp;

  /** The geographic location/seat of the court. */
  @XmlElement(name = "gerichtsort", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String gerichtsort;

  /** The specific judicial body or chamber (Spruchkörper) within the court. */
  @XmlElement(name = "spruchkoerper", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Spruchkoerper spruchkoerper;
}
