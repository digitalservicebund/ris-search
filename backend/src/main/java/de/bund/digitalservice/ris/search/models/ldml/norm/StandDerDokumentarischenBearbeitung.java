package de.bund.digitalservice.ris.search.models.ldml.norm;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a {@code standDerDokumentarischenBearbeitung} element of a norm work's {@code
 * metadaten.xml} file.
 */
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class StandDerDokumentarischenBearbeitung {

  @XmlValue private String text;

  @XmlAttribute(name = "domainTerm")
  private final String domainTerm = "Stand der dokumentarischen Bearbeitung";

  public StandDerDokumentarischenBearbeitung(String text) {
    this.text = text;
  }
}
