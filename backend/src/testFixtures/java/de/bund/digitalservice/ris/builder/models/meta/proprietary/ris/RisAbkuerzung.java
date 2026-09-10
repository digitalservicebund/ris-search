package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.NoArgsConstructor;

/** Represents the {@code ris:abkuerzung} element, the norm's internal RIS abbreviation. */
@NoArgsConstructor
public class RisAbkuerzung {

  @XmlAttribute private String refersTo = "interne-abkuerzung";

  @XmlValue private String abbreviation = "RisAbk";

  public RisAbkuerzung(String abbreviation) {
    this.abbreviation = abbreviation;
  }
}
