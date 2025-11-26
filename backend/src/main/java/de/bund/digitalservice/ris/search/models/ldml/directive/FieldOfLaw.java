package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a field of law in an XML-based model.
 *
 * <p>The `FieldOfLaw` class is used for serializing and deserializing fields of law associated with
 * a particular document in an XML structure. It has two primary attributes:
 *
 * <p>- `notation`: A string representation of the law field, typically used for classification or
 * identification purposes. This is annotated with `@XmlAttribute`, indicating that it will be
 * represented as an attribute in the XML structure. - `value`: The text value or description
 * associated with the field of law. This is annotated with `@XmlValue`, making it the actual
 * content of the XML element.
 *
 * <p>This class uses JAXB annotations for mapping the properties to XML elements and Lombok
 * annotations to generate boilerplate code for getters and setters.
 */
@Getter
@Setter
public class FieldOfLaw {
  @XmlAttribute private String notation;

  @XmlValue private String value;
}
