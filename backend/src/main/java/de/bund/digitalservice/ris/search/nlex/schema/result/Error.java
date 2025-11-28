package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents an error in the context of the application's search schema.
 *
 * <p>The {@code Error} class encapsulates information about an error, including details about the
 * cause of the error. It is primarily used to provide structured error information in response
 * objects.
 *
 * <p>Fields: - {@code cause}: A description or identifier for the cause of the error, often used
 * for debugging or reporting purposes.
 *
 * <p>Constants: - {@code STANDARD_ERROR_NO_SEARCHTERM}: Represents a predefined error code used
 * when no search term is provided.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps fields
 * directly to XML elements or attributes. - {@code @XmlAttribute}: Indicates that the {@code cause}
 * field is mapped as an XML attribute. - Lombok annotations {@code @Data} and
 * {@code @Accessors(chain = true)}: Generate boilerplate methods like getters and setters, and
 * support method chaining.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Error {
  public static final String STANDARD_ERROR_NO_SEARCHTERM = "1";

  @XmlAttribute String cause;
}
