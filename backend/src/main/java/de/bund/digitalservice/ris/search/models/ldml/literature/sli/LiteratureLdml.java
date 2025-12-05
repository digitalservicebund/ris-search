package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

/**
 * Represents the root-level model for literature-specific Akoma Ntoso (LegalDocumentML) documents,
 * used for XML serialization and deserialization.
 *
 * <p>This class is annotated with JAXB annotations to map its fields and structure to the Akoma
 * Ntoso XML schema. It contains the primary document content, represented by the {@code Doc} class,
 * which includes metadata and main body information.
 *
 * <p>The {@code LiteratureLdml} class is designed to facilitate the processing and exchange of
 * legal or literature-related documents within standardized XML formats.
 *
 * <p>Annotations: - {@code @XmlRootElement}: Specifies the root XML element for the class, using
 * the Akoma Ntoso namespace. - {@code @XmlAccessorType}: Configures the JAXB access type to FIELD,
 * enabling direct mapping of class fields to XML elements.
 *
 * <p>Field: - doc: Represents the document content and metadata, serialized as the "doc" element.
 * It is associated with the Akoma Ntoso namespace.
 */
@Getter
@XmlRootElement(name = "akomaNtoso", namespace = LiteratureNamespaces.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class LiteratureLdml {

  @XmlElement(name = "doc", namespace = LiteratureNamespaces.AKN_NS)
  private Doc doc;
}
