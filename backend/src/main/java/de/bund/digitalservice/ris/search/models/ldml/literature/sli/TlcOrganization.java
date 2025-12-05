package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
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
 * organization, serialized within a specific namespace.
 */
@Getter
public class TlcOrganization {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "name", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private String name;
}
