package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RisAbkuerzung {

  @Builder.Default
  @XmlAttribute(name = "refersTo")
  private String refersTo = "interne-abkuerzung";

  @Builder.Default @XmlValue private String abbreviation = "RisAbk";
}
