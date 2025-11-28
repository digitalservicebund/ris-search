package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents pagination information for search results.
 *
 * <p>The {@code Page} class is used to model pagination-related data, such as the current page
 * number and the size of each page. This class is typically used within navigation or result
 * structures to provide metadata about the paginated data.
 *
 * <p>Fields: - {@code number}: Represents the current page number. - {@code size}: Indicates the
 * number of items per page.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields of this class directly to XML elements or attributes. - {@code @XmlAttribute}: Maps fields
 * as XML attributes in the serialized data. - Lombok annotations {@code @Data} and
 * {@code @Accessors(chain = true)}: Auto-generates boilerplate code such as getters and setters and
 * allows method chaining for a fluent API style.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Page {

  @XmlAttribute private int number;

  @XmlAttribute private int size;
}
