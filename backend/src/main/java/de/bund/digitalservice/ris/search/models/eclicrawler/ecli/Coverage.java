package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the geographical or jurisdictional coverage of a resource.
 *
 * <p>This class is part of the ECLI metadata structure and adheres to the standards for XML
 * serialization and deserialization using JAXB annotations. It encapsulates information about the
 * coverage of a resource, including its textual representation and the associated language.
 *
 * <p>The `value` field represents the main content describing the coverage, and the `lang` field
 * specifies the language of the textual content.
 *
 * <p>By default: - The `value` field is set to "deutschland". - The `lang` field is set to German
 * ("de").
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Coverage {
  @XmlValue private String value = "deutschland";

  @XmlAttribute private String lang = "de";
}
