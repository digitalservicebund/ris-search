package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a single FRBRNumber element within the context of the FRBR (Functional Requirements
 * for Bibliographic Records) framework.
 *
 * <p>Fields: - value: The numeric value Serialized as an XML attribute.
 */
@Getter
public class FRBRNumber {

  @XmlAttribute String value;
}
