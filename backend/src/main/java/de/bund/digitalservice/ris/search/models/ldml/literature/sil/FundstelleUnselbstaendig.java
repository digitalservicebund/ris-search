package de.bund.digitalservice.ris.search.models.ldml.literature.sil;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a dependency reference within a periodical publication.
 *
 * <p>This class is primarily used for the serialization and deserialization of XML attributes
 * specific to unselbstständige (dependent) references in legal or bibliographic documents,
 * leveraging JAXB (Jakarta XML Binding).
 *
 * <p>Fields: - periodikum: The name or identifier of the periodical in which the reference is
 * published. Serialized as an XML attribute with the name "periodikum". - zitstelle: The citation
 * location or reference detail within the periodical. Serialized as an XML attribute with the name
 * "zitatstelle".
 */
@Getter
public class FundstelleUnselbstaendig {

  @XmlAttribute(name = "periodikum")
  private String periodikum;

  @XmlAttribute(name = "zitatstelle")
  private String zitstelle;
}
