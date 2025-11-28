package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a generic block with a name and a value.
 *
 * <p>This class is designed for use in XML-based data serialization and deserialization, with the
 * following annotations to map its properties to XML attributes or values: - `name`: Annotated with
 * `@XmlAttribute` to indicate that it should be represented as an attribute in the XML structure. -
 * `value`: Annotated with `@XmlValue` to denote that it represents the actual value of the XML
 * element.
 */
@Getter
@Setter
public class Block {
  @XmlAttribute private String name;

  @XmlValue private String value;
}
