package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import static de.bund.digitalservice.ris.builder.NormTestDataBuilder.RIS_NS;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the {@code ris:legalDocML.de_metadaten} element, holding RIS-specific norm metadata.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RisMetadata {

  private static class EmptyElement {}

  @Builder.Default
  @XmlElement(name = "abkuerzung", namespace = RIS_NS)
  private RisAbkuerzung internalAbbreviation = RisAbkuerzung.builder().build();

  public void setAbbreviation(String abbreviation) {
    if (abbreviation == null) {
      this.internalAbbreviation = null;
    } else {
      this.internalAbbreviation = RisAbkuerzung.builder().abbreviation(abbreviation).build();
    }
  }

  @XmlElement(name = "inkraft", namespace = RIS_NS)
  private RisDate inForceDate;

  public void setInForce(String date) {
    this.inForceDate = RisDate.builder().date(date).build();
  }

  @XmlElement(name = "ausserkraft", namespace = RIS_NS)
  private RisDate outOfForceDate;

  public void setOutOfForce(String date) {
    this.outOfForceDate = RisDate.builder().date(date).build();
  }

  @Setter
  @XmlElement(name = "vollzitat", namespace = RIS_NS)
  private String fullCitation;

  @XmlElement(namespace = RIS_NS)
  private EmptyElement bedingtesInkrafttreten;

  public void setBedingtesInkrafttreten() {
    this.bedingtesInkrafttreten = new EmptyElement();
  }

  @XmlElement(namespace = RIS_NS)
  private EmptyElement gegenstandslos;

  public void setGegenstandslos() {
    this.gegenstandslos = new EmptyElement();
  }
}
