package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a language entity used in the context of structured metadata, adhering to a predefined
 * format.
 *
 * <p>The `Language` class encapsulates two primary components: - `languageType`: Specifies the type
 * of language context, defaulting to "authoritative". - `value`: Holds the value of the language,
 * with the default set to German ("de").
 *
 * <p>This class supports XML serialization and deserialization using JAXB annotations, making it
 * suitable for integration with systems requiring structured language metadata.
 *
 * <p>Constants: - `GERMAN`: Represents the default language value as "de".
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Language {
  public static final String GERMAN = "de";

  @XmlAttribute private String languageType = "authoritative";

  @XmlValue private String value = GERMAN;
}
