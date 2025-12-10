package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.MainBody;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents the root XML structure for a document, containing key metadata and content elements as
 * part of the Akoma Ntoso (LegalDocumentML) framework.
 *
 * <p>This class serves as the primary container for document information, including metadata about
 * the document and its main body content. It is designed to be used in environments where XML
 * serialization and deserialization are required for processing legal or literature documents.
 *
 * <p>Fields: - meta: The metadata section of the document, represented by the {@code Meta} class.
 * This section contains details such as identification, classification, and references for the
 * document. Serialized under the "meta" XML tag. - mainBody: The main content of the document,
 * represented by the {@code MainBody} class. This section includes the substantive body of the
 * document as mixed content. Serialized under the "mainBody" XML tag.
 *
 * <p>The XML elements are annotated to conform to the Akoma Ntoso namespace for improved
 * interoperability with legal documentation systems.
 */
@Getter
public class Doc {
  @XmlElement(name = "meta", namespace = LiteratureNamespaces.AKN_NS)
  private Meta meta;

  @XmlElement(name = "mainBody", namespace = LiteratureNamespaces.AKN_NS)
  private MainBody mainBody;
}
