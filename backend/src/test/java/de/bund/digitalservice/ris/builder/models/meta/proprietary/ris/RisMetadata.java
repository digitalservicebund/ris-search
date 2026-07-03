package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import static de.bund.digitalservice.ris.builder.NormTestDataBuilder.RIS_NS;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RisMetadata {

  @Builder.Default
  @XmlElement(name = "abkuerzung", namespace = RIS_NS)
  private RisAbkuerzung internalAbbreviation = RisAbkuerzung.builder().build();

  public void setAbbreviation(String abbreviation) {
    this.internalAbbreviation = RisAbkuerzung.builder().abbreviation(abbreviation).build();
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
}
