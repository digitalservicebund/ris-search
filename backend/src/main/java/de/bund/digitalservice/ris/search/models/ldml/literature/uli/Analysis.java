package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents the analysis section in the metadata of a document within the Akoma Ntoso
 * (LegalDocumentML) framework.
 *
 * <p>This class is designed to encapsulate the "analysis" element, which is an integral part of the
 * metadata hierarchy in serialization and deserialization processes. The analysis contains detailed
 * metadata, primarily focusing on references within the document.
 *
 * <p>Fields: - otherReferences: A list of {@link OtherReferences} objects representing detailed
 * references associated with the document. This is serialized under the "otherReferences" XML tag
 * within the Akoma Ntoso (AKN) namespace.
 */
@Getter
public class Analysis {

  @XmlElement(name = "otherReferences", namespace = LiteratureNamespaces.AKN_NS)
  private List<OtherReferences> otherReferences;
}
