package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a case law reference with a unique reference number.
 *
 * <p>The `CaselawReference` class is designed for XML-based data serialization and deserialization.
 * It includes the following annotated property:
 *
 * <p>- `referenceNumber`: Annotated with `@XmlAttribute`, it represents the unique identifier or
 * reference number associated with the case law.
 *
 * <p>This class uses Lombok annotations for generating getters and setters.
 */
@Getter
@Setter
public class CaselawReference {

  @XmlAttribute private String referenceNumber;
}
