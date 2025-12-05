package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;

/**
 * Represents the FRBR (Functional Requirements for Bibliographic Records) expression level in the
 * bibliographic hierarchy.
 *
 * <p>This class is utilized for the text block container
 *
 * <p>Fields: - value: content of the text block
 */
@Getter
public class Block {
  @XmlValue String value;
}
