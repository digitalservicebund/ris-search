package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents an identifier within the ECLI metadata structure.
 *
 * <p>The `Identifier` class encapsulates the main value of the identifier along with its associated
 * metadata, such as language and format. It is annotated for XML serialization and deserialization
 * using JAXB, making it suitable for use in structured metadata systems.
 *
 * <p>Key Features: - `value`: Holds the identifier's textual content. - `lang`: Specifies the
 * language of the identifier, defaulting to German ("de"). - `format`: Specifies the format of the
 * identifier, defaulting to "text/html".
 *
 * <p>This class is primarily used to represent identifiers in the context of ECLI-compliant
 * structured metadata and ensures compatibility with XML-based data exchanges.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Identifier {

  @XmlAttribute private String lang = Language.GERMAN;
  @XmlAttribute private String format = "text/html";

  @XmlValue private String value;
}
