package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a keyword with attributes that can be used for classification or metadata purposes.
 *
 * <p>This class is designed to model a keyword object with its associated attributes: - `value`:
 * Stores the actual keyword value. - `showAs`: Represents how the keyword should be displayed.
 *
 * <p>Both attributes are annotated with `@XmlAttribute`, making them suitable for XML
 * serialization/deserialization.
 */
@Getter
@Setter
public class Keyword {

  @XmlAttribute private String value;

  @XmlAttribute private String showAs;
}
