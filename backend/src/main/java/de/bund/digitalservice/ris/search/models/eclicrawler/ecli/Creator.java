package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the creator of a resource in the metadata scheme.
 *
 * <p>This class is used to encapsulate information about the creator, including the name or
 * descriptor of the creator and the associated language of the textual content. Serialization and
 * deserialization to and from XML is supported through JAXB annotations.
 *
 * <p>The `value` field holds the main content representing the creator, and the `lang` field
 * specifies the language of that content.
 *
 * <p>By default, the language is set to German ("de").
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Creator {
  @XmlValue private String value;

  @XmlAttribute private String lang = Language.GERMAN;
}
