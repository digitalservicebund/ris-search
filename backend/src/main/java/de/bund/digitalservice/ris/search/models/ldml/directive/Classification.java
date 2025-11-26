package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the classification of a document or a specific element within a document.
 *
 * <p>The `Classification` class is annotated for XML serialization and deserialization,
 * specifically within the context of the AdministrativeDirectiveLdml schema. It includes properties
 * to define the source of the classification and associated keywords.
 *
 * <p>Key Features: - `source`: An XML attribute representing the origin or source of the
 * classification information. - `keywords`: A list of `Keyword` objects that provide additional
 * metadata or descriptors related to the classification. These are serialized as XML elements with
 * a specified namespace.
 */
@Getter
@Setter
public class Classification {

  @XmlAttribute private String source;

  @XmlElement(name = "keyword", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<Keyword> keywords;
}
