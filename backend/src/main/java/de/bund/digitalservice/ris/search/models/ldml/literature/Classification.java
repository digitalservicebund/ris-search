package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents a classification element within the document metadata structure, typically found in
 * the Akoma Ntoso (LegalDocumentML) framework.
 *
 * <p>This class encapsulates metadata related to the classification of a document. It is used
 * during serialization and deserialization processes leveraging JAXB (Jakarta XML Binding). The
 * classification contains a source attribute and a list of associated keywords.
 *
 * <p>Fields: - source: A string representing the source of the classification, serialized as an XML
 * attribute. - keywords: A list of {@link Keyword} objects representing associated keywords. These
 * keywords are serialized under the "keyword" XML element, utilizing the Akoma Ntoso XML namespace.
 */
@Getter
public class Classification {

  @XmlAttribute private String source;

  @XmlElement(name = "keyword", namespace = LiteratureNamespaces.AKN_NS)
  private List<Keyword> keywords;
}
