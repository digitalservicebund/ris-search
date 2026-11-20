package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;

/**
 * An entry of one of the role lists in the proprietary RIS metadata (e.g. {@code
 * ris:verfasserListe/ris:verfasser}, {@code ris:urheberListe/ris:urheber}).
 *
 * <p>The text content is the display name, {@code refersTo} points to the {@code ris:person},
 * {@code ris:organisation} or {@code ris:kongress} with the matching eId (prefixed with "#").
 */
@Getter
public class RisRoleItem {

  @XmlAttribute(name = "refersTo")
  private String refersTo;

  @XmlValue private String value;
}
