package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the type of a document with related metadata.
 *
 * <p>This class is used for XML data serialization and deserialization with the following
 * annotations: - `category`: Annotated with `@XmlAttribute`, represents the category of the
 * document type. - `longTitle`: Annotated with `@XmlAttribute`, represents the long title of the
 * document type. - `value`: Annotated with `@XmlValue`, captures the main value associated with the
 * document type.
 *
 * <p>It is a data model class for maintaining the document type's structure and attributes in XML
 * processing. This ensures that XML elements are correctly mapped to the respective fields in the
 * class during parsing or generation.
 */
@Getter
@Setter
public class DocumentType {

  @XmlAttribute private String category;

  @XmlAttribute private String longTitle;

  @XmlValue private String value;
}
