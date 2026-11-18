package de.bund.digitalservice.ris.search.models.ldml.norm;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a {@code standangabe} element of a norm work's {@code metadaten.xml} file. */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class Standangabe {

  @XmlValue private String text;

  @XmlAttribute(name = "typ")
  private String typ;
}
