package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a legislative authority or norm-setting body.
 *
 * <p>This class is used to model and serialize/deserialize information about the entity responsible
 * for issuing norms or regulations. It includes the following attributes:
 *
 * <p>- `staat`: The country or state associated with the legislative or norm-setting body. -
 * `organ`: The specific organ or authority within the state responsible for the norms.
 *
 * <p>Both attributes are mapped to XML elements as attributes using the `@XmlAttribute` annotation.
 */
@Getter
@Setter
public class Normgeber {
  @XmlAttribute private String staat;

  @XmlAttribute private String organ;
}
