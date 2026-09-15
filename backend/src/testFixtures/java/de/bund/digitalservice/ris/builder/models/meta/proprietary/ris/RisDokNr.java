package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Represents {@code ris:dokNr} mapping an eId to the immutable dokNr */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RisDokNr {
  @XmlAttribute private String source;

  @XmlValue private String value;
}
