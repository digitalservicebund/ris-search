package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents the language element in the FRBR (Functional Requirements for Bibliographic Records)
 * context.
 *
 * <p>This class is used for handling the language attribute associated with a bibliographic work
 */
@Getter
public class FrbrLanguage {

  @XmlAttribute private String language;
}
