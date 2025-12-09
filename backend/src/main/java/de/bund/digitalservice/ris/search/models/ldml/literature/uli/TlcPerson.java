package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a TLC (Thesaurus of Legal Concepts) person entity with attributes that specify an ID
 * and a name.
 *
 * <p>This class is used within the context of legal documentation metadata and may be serialized or
 * deserialized from XML using the specified attribute names and namespaces.
 *
 * <p>Attributes: - eId: A unique identifier assigned to the person. - name: The name of the person,
 * serialized within the defined namespace.
 */
@Getter
public class TlcPerson {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "name", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private String name;
}
