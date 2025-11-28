package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the FRBRThis element in a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FrbrThis {
  @XmlAttribute private String value;
}
