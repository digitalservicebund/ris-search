package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents an external URL in the search schema result.
 *
 * <p>The {@code ExternUrl} class encapsulates details about an external URL, including its
 * hyperlink reference, format, and display text. This is primarily used to model external links
 * related to search results or documents.
 *
 * <p>Fields: - {@code href}: The hyperlink reference (URL) as a string. - {@code format}: Specifies
 * the format or type of the external URL (e.g., "HTML", "PDF", etc.). - {@code display}: The
 * display text associated with the URL.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields in this class to corresponding XML elements or attributes. - {@code @XmlAttribute}:
 * Indicates that all fields are mapped as XML attributes within the serialized XML structure. -
 * Lombok annotations {@code @Data} and {@code @Accessors(chain = true)}: Auto-generates boilerplate
 * methods like getters, setters, and supports method chaining for fluent API usage.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
@XmlType(propOrder = {"format", "display", "href"})
public class ExternUrl {

  @XmlAttribute private String href;

  @XmlAttribute private String format;

  @XmlAttribute private String display;
}
