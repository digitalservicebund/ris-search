package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents an independent citation or reference point within a document.
 *
 * <p>This class is used for handling metadata related to independent references (selbstständig),
 * specifically those with a title and a citation location. It is primarily designed for XML
 * serialization and deserialization using JAXB (Jakarta XML Binding).
 *
 * <p>Fields: - titel: The title of the reference, serialized as an XML attribute named "titel". -
 * zitstelle: The citation location of the reference, serialized as an XML attribute named
 * "zitatstelle".
 */
@Getter
public class FundstelleSelbstaendig {

  @XmlAttribute(name = "titel")
  private String titel;

  @XmlAttribute(name = "zitatstelle")
  private String zitstelle;
}
