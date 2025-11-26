package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a name-value pair element used in XML structures.
 *
 * <p>The `FrbrNameValueElement` class is designed for XML-based serialization and deserialization.
 * It encapsulates a pair of attributes: `name` and `value`, which are mapped to XML attributes
 * using JAXB annotations.
 *
 * <p>Fields: - `name`: Represents the name of the element. Annotated with `@XmlAttribute` for XML
 * mapping. - `value`: Represents the value associated with the name. Annotated with
 * `@XmlAttribute`.
 *
 * <p>This class utilizes Lombok annotations (@Getter and @Setter) to automatically generate getters
 * and setters for its fields.
 */
@Getter
@Setter
public class FrbrNameValueElement {

  @XmlAttribute private String name;

  @XmlAttribute private String value;
}
