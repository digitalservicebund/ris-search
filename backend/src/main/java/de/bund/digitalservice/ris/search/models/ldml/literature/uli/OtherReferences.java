package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents a structure for holding references within a document.
 *
 * <p>The class is designed to model "other references" in metadata or document analysis. It
 * provides attributes for the source of references and encapsulates a list of implicit references,
 * which offer detailed metadata about specific references used in the document context.
 *
 * <p>Fields: - source: Represents the origin or source of the references. It is mapped as an XML
 * attribute. - implicitReferences: A list of {@link ImplicitReference} objects that detail specific
 * references implicitly associated with the document. It is serialized as an XML element with a
 * predefined namespace.
 */
@Getter
public class OtherReferences {

  @XmlAttribute(name = "source")
  private String source;

  @XmlElement(name = "implicitReference", namespace = LiteratureNamespaces.AKN_NS)
  private List<ImplicitReference> implicitReferences;
}
