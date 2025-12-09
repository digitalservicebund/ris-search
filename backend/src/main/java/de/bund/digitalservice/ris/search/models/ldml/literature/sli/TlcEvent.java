package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a TLC (Thesaurus of Legal Concepts) event with specific attributes that define its
 * identifier and display properties.
 *
 * <p>This class is primarily used within the context of legal metadata structures and is designed
 * for serialization and deserialization of XML elements, leveraging JAXB (Jakarta XML Binding).
 *
 * <p>Fields: - eId: A unique identifier for the TLC event, serialized as an XML attribute with the
 * name "eId". - showAs: A descriptive property indicating how the event should be presented,
 * serialized as an XML attribute with the name "showAs".
 */
@Getter
public class TlcEvent {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "showAs")
  private String showAs;
}
