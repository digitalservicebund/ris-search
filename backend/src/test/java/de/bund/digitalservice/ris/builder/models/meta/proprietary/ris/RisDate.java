package de.bund.digitalservice.ris.builder.models.meta.proprietary.ris;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RisDate {

  @Builder.Default @XmlAttribute private String date = "2024-01-06";
}
