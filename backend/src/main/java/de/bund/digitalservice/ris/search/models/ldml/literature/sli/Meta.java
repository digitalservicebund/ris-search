package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

/**
 * Represents the metadata section within the Akoma Ntoso (LegalDocumentML) framework.
 *
 * <p>The Meta class acts as the container for the critical metadata components associated with a
 * document. It facilitates the organization and serialization of various metadata elements into
 * compliant XML structures, leveraging the namespace definitions provided by the Akoma Ntoso
 * standard.
 *
 * <p>This class includes the following key metadata elements:
 *
 * <p>- Identification: Represents the identification of the document based on the FRBR (Functional
 * Requirements for Bibliographic Records) standard, focusing on "work" and "expression" levels. -
 * Proprietary: Encapsulates proprietary metadata information relevant to RIS (Legal Information
 * System) documents, particularly for dependent publications. - Classifications: A list of
 * classification elements that categorize the document based on predefined criteria or sources. -
 * Analysis: Contains analytical metadata focusing on the references and citations within the
 * document. - References: Groups detailed references to persons, organizations, and events
 * associated with the document's metadata.
 *
 * <p>All fields are JAXBElement annotated, ensuring proper mapping to the Akoma Ntoso namespace
 * during XML serialization and deserialization processes.
 */
@Getter
public class Meta {

  @XmlElement(name = "identification", namespace = LiteratureNamespaces.AKN_NS)
  private Identification identification;

  @XmlElement(name = "proprietary", namespace = LiteratureNamespaces.AKN_NS)
  private Proprietary proprietary;

  @XmlElement(name = "classification", namespace = LiteratureNamespaces.AKN_NS)
  private List<Classification> classifications;

  @XmlElement(name = "analysis", namespace = LiteratureNamespaces.AKN_NS)
  private Analysis analysis;

  @XmlElement(name = "references", namespace = LiteratureNamespaces.AKN_NS)
  private References references;

  @XmlElementWrapper(namespace = LiteratureNamespaces.AKN_NS)
  @XmlElement(name = "note", namespace = LiteratureNamespaces.AKN_NS)
  private List<Note> notes;
}
