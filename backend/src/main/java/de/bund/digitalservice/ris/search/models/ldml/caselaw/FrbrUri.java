package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the FRBRuri element in the case law LDML format. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FrbrUri {
  @XmlAttribute private String value;
}
