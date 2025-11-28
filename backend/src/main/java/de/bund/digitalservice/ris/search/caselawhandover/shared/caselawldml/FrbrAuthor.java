package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents an author in the FRBR (Functional Requirements for Bibliographic Records) model. The
 * author is identified by a reference (href).
 */
@NoArgsConstructor
@Getter
public class FrbrAuthor {
  @XmlAttribute private String href = "attributsemantik-noch-undefiniert";
}
