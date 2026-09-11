package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import static de.bund.digitalservice.ris.builder.NormTestDataBuilder.RIS_NS;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the {@code ris:legalDocML.de_metadaten} element, holding RIS-specific norm metadata.
 */
@NoArgsConstructor
public class RisMetadata {

  private static class EmptyElement {}

  @XmlElement(name = "abkuerzung", namespace = RIS_NS)
  private RisAbkuerzung internalAbbreviation = new RisAbkuerzung();

  /**
   * Sets the RIS-internal abbreviation, or removes it if {@code null}.
   *
   * @param abbreviation the abbreviation value, or {@code null} to remove it
   */
  public void setAbbreviation(String abbreviation) {
    if (abbreviation == null) {
      this.internalAbbreviation = null;
    } else {
      this.internalAbbreviation = new RisAbkuerzung(abbreviation);
    }
  }

  @XmlElement(name = "inkraft", namespace = RIS_NS)
  private RisDate inForceDate;

  /**
   * Sets the in-force date element.
   *
   * @param date the in-force date value
   */
  public void setInForce(String date) {
    this.inForceDate = new RisDate(date);
  }

  @XmlElement(name = "ausserkraft", namespace = RIS_NS)
  private RisDate outOfForceDate;

  /**
   * Sets the out-of-force date element.
   *
   * @param date the out-of-force date value
   */
  public void setOutOfForce(String date) {
    this.outOfForceDate = new RisDate(date);
  }

  @Setter
  @XmlElement(name = "vollzitat", namespace = RIS_NS)
  private String fullCitation;

  @XmlElement(namespace = RIS_NS)
  private EmptyElement bedingtesInkrafttreten;

  /** Marks the norm as {@code bedingtesInkrafttreten}. */
  public void setBedingtesInkrafttreten() {
    this.bedingtesInkrafttreten = new EmptyElement();
  }

  @XmlElement(namespace = RIS_NS)
  private EmptyElement gegenstandslos;

  /** Marks the norm as {@code gegenstandslos}. */
  public void setGegenstandslos() {
    this.gegenstandslos = new EmptyElement();
  }
}
