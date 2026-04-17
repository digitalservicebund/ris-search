package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents an alias in the FRBR (Functional Requirements for Bibliographic Records) model. An
 * alias consists of a name and a value, where the name is optional.
 */
@NoArgsConstructor
@Getter
public class FrbrAlias {
  @XmlAttribute private String name;
  @XmlAttribute private String value;
}
