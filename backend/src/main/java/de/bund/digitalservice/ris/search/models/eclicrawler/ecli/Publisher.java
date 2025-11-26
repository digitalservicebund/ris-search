package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a publisher entity in the metadata scheme. The publisher provides details about the
 * entity responsible for publishing a resource.
 *
 * <p>This class is annotated for XML binding, allowing its fields to be serialized or deserialized
 * into XML. The `@XmlAttribute` annotation is used to define an attribute for the language, and the
 * `@XmlValue` annotation is used to hold the main textual content of the publisher.
 *
 * <p>The default language is set to German ("de"), and the default value is "BMJV."
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Publisher {
  @XmlAttribute private String lang = Language.GERMAN;
  @XmlValue private String value = "BMJV";
}
