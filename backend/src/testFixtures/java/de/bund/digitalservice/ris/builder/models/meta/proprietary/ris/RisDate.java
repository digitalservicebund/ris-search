package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents a RIS date element, e.g. {@code ris:inkraft} or {@code ris:ausserkraft}. */
@AllArgsConstructor
@NoArgsConstructor
public class RisDate {

  @XmlAttribute private String date = "2024-01-06";
}
