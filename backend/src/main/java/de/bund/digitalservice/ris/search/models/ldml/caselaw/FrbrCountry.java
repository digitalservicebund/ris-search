package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the FRBRcountry element in the case law LDML format. */
@NoArgsConstructor
@Getter
public class FrbrCountry {
  @XmlAttribute private String value = "de";
}
