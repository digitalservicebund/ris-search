package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the long title of a document within the context of an XML structure.
 *
 * <p>The `LongTitle` class is designed for XML serialization and deserialization, specifically for
 * use within the `AdministrativeDirectiveLdml` schema. It includes the following property:
 *
 * <p>- `block`: Represents a generic block element. It is annotated with `@XmlElement` to associate
 * it with an XML element in the namespace defined by `AdministrativeDirectiveLdml.AKN_NS`.
 *
 * <p>This class utilizes JAXB annotations to facilitate the mapping of the `block` property to its
 * corresponding XML representation.
 */
@Getter
@Setter
public class LongTitle {
  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  Block block;
}
