package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a type entity in the ECLI metadata structure, often used to describe the type or
 * nature of specific metadata elements.
 *
 * <p>This class encapsulates the type's textual content and its associated language. It adheres to
 * XML serialization and deserialization standards using JAXB annotations, ensuring that it can be
 * integrated into structured metadata systems.
 *
 * <p>Fields: - `value`: The main content representing the type, defaulting to
 * "Gerichtsentscheidung". - `lang`: Specifies the language of the `value` content, defaulting to
 * German ("de").
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Type {
  @XmlAttribute private String lang = Language.GERMAN;
  @XmlValue private String value = "Gerichtsentscheidung";
}
