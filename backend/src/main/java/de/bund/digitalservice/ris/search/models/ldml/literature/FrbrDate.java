package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FrbrDate {
  @XmlAttribute private String date;
  @XmlAttribute private String name = "erfassungsdatum";

  public FrbrDate(String date) {
    this.date = date;
  }
}
