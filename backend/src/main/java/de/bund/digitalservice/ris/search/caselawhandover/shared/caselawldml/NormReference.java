package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a reference to a specific norm within an XML structure.
 *
 * <p>The NormReference class is a model designed for XML serialization and deserialization. It
 * contains a single property:
 *
 * <p>- `singleNorm`: A string representing the reference to a specific norm. This property is
 * mapped to an XML attribute using the `@XmlAttribute` annotation.
 */
@Getter
@Setter
public class NormReference {

  @XmlAttribute private String singleNorm;
}
