package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a TLC (Thesaurus of Legal Concepts) organization entity with attributes that specify
 * an ID and a name.
 *
 * <p>This class is used within the context of legal documentation metadata and is intended for
 * serialization and deserialization of XML elements with the specified attribute names and
 * namespaces.
 *
 * <p>Attributes: - eId: A unique identifier assigned to the organization. - name: The name of the
 * organization, serialized within a specific namespace. - showAs: The display representation of the
 * organization.
 */
@Getter
public class TlcOrganization {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "name", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String name;

  @XmlAttribute(name = "showAs")
  private String showAs;
}
