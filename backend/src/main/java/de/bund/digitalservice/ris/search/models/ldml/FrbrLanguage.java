package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the FRBRlanguage element in the case law LDML format. */
@NoArgsConstructor
@Getter
public class FrbrLanguage {
  @XmlAttribute private String language = "deu";
}
