package de.bund.digitalservice.ris.search.models.ldml.literature.sil;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a keyword element in the context of document metadata.
 *
 * <p>This class is primarily used for serialization and deserialization of keyword elements within
 * XML structures, leveraging JAXB (Jakarta XML Binding).
 *
 * <p>Attributes: - value: The string value of the keyword, annotated to be serialized as an XML
 * attribute.
 */
@Getter
public class Keyword {

  @XmlAttribute private String value;
}
