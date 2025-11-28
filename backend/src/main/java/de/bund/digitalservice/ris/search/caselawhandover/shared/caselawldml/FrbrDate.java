package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a date in the FRBR (Functional Requirements for Bibliographic Records) model. The date
 * has a value and a name, where the name defaults to "entscheidungsdatum".
 */
@NoArgsConstructor
@Getter
public class FrbrDate {
  @XmlAttribute private String date;
  @XmlAttribute private String name = "entscheidungsdatum";

  public FrbrDate(String date) {
    this.date = date;
  }
}
