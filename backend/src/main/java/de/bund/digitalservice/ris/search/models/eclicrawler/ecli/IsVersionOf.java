package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a versioning metadata element for documenting the relationship between a resource and
 * its previous versions in a structured metadata scheme.
 *
 * <p>This class is part of the ECLI metadata model, adhering to the standards for XML serialization
 * and deserialization using JAXB annotations. It primarily captures information about a prior
 * version of a resource, including the associated country and court details.
 *
 * <p>Fields: - `value`: Specifies the unique identifier or reference to the preceding version of
 * the resource. - `country`: Indicates the country associated with the resource. Defaults to "DE".
 * - `court`: Represents the court associated with the resource, providing additional jurisdictional
 * context.
 *
 * <p>Constants: - `COUNTRY_DE`: A constant representing the default country "DE" for German
 * jurisdiction.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class IsVersionOf {
  public static final String COUNTRY_DE = "DE";

  @XmlAttribute String value;

  @XmlElement String country = "DE";

  @XmlElement String court;
}
