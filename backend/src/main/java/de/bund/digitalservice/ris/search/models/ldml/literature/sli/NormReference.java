package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.util.Date;
import lombok.Getter;

/**
 * Represents a reference to a norm, including its abbreviation, version date, and associated single
 * norm.
 *
 * <p>This class is designed to be serialized and deserialized in the context of XML structures,
 * utilizing JAXB (Jakarta XML Binding). Each field corresponds to an XML attribute, allowing
 * precise control over serialization and deserialization.
 *
 * <p>Attributes: - abbreviation: The abbreviation of the norm, annotated to be serialized as an XML
 * attribute. - dateOfVersion: The date of the norm's specific version, serialized as an XML
 * attribute. - singleNorm: A reference to a specific single norm, serialized as an XML attribute.
 */
@Getter
public class NormReference {

  @XmlAttribute(name = "abbreviation")
  private String abbreviation;

  @XmlAttribute(name = "dateOfVersion")
  private Date dateOfVersion;

  @XmlAttribute(name = "singleNorm")
  private String singleNorm;
}
