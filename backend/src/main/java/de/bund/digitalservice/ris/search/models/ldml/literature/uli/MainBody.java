package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.MixedContentNode;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Represents the main body of a document in the context of the Akoma Ntoso (LegalDocumentML)
 * framework.
 *
 * <p>This class is designed to handle the mixed content of the main body within a document. It
 * extends the {@code MixedContentNode} class, enabling the representation of complex content that
 * may contain textual data as well as structured XML elements.
 *
 * <p>The {@code MainBody} functionality is closely tied to XML serialization and deserialization
 * processes, with an XML root element named "mainBody" as defined by the JAXB annotation
 * {@code @XmlRootElement}.
 *
 * <p>This class is used as part of the larger document structure, often embedded in a parent
 * document class such as {@code Doc}, to encapsulate the substantive portion of the document's
 * content. The mixed content enables flexibility in organizing and processing the main content of
 * legal or literature documents.
 */
@XmlRootElement(name = "mainBody")
public class MainBody extends MixedContentNode {}
