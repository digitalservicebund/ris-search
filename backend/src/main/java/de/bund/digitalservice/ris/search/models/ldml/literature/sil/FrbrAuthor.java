package de.bund.digitalservice.ris.search.models.ldml.literature.sil;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents an author in the FRBR (Functional Requirements for Bibliographic Records) context.
 *
 * <p>This class is primarily used for the serialization and deserialization of FRBR author elements
 * within XML structures, leveraging JAXB (Jakarta XML Binding).
 *
 * <p>Fields: - as: The role or context in which the author is specified, serialized as an XML
 * attribute with the name "as". - href: A reference or link associated with the author, serialized
 * as an XML attribute with the name "href".
 */
@Getter
public class FrbrAuthor {

  @XmlAttribute(name = "as")
  private String as;

  @XmlAttribute(name = "href")
  private String href;
}
