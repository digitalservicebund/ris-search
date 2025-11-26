package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a paragraph in the search schema result.
 *
 * <p>The {@code Para} class models a paragraph with its textual value and associated roles. This
 * class is used to represent segments of content, typically within the {@code Content} class.
 *
 * <p>Fields: - {@code value}: The textual content of the paragraph. - {@code roles}: Metadata
 * representing roles or classifications associated with the paragraph. This may be used to identify
 * the type or purpose of the paragraph.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields in this class to corresponding XML elements/attributes. - {@code @XmlValue}: Indicates
 * that the {@code value} field represents the text content of the XML element. -
 * {@code @XmlAttribute}: Maps the {@code roles} field as an XML attribute. - Lombok annotations
 * {@code @Data} and {@code @Accessors(chain = true)}: Auto-generates getters, setters, and supports
 * method chaining.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Para {

  @XmlValue private String value;

  @XmlAttribute private String roles;
}
