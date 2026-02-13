package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.TlcEvent;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.TlcOrganization;
import de.bund.digitalservice.ris.search.models.ldml.literature.sli.TlcPerson;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents a collection of references in the context of legal documentation metadata.
 *
 * <p>This class groups lists of references to persons, organizations, and events, which are
 * relevant for metadata about legal or literary documents. Elements of each list are defined within
 * the specific namespace for legal annotations.
 *
 * <p>Fields: - tlcPersons: A list of TLC (Thesaurus of Legal Concepts) person references,
 * serialized with the XML element name "TLCPerson". - tlcOrganizations: A list of TLC organization
 * references, serialized with the XML element name "TLCOrganization". - tlcEvents: A list of TLC
 * event references, serialized with the XML element name "TLCEvent".
 *
 * <p>Each list is annotated for JAXB (Jakarta XML Binding), to specify the corresponding XML
 * element name and namespace used during the serialization/deserialization process.
 */
@Getter
public class References {

  @XmlElement(name = "TLCPerson", namespace = LiteratureNamespaces.AKN_NS)
  private List<TlcPerson> tlcPersons;

  @XmlElement(name = "TLCOrganization", namespace = LiteratureNamespaces.AKN_NS)
  private List<TlcOrganization> tlcOrganizations;

  @XmlElement(name = "TLCEvent", namespace = LiteratureNamespaces.AKN_NS)
  private List<TlcEvent> tlcEvents;
}
