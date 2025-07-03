package de.bund.digitalservice.ris.search.eclicrawler.schema.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Language {
  public static final String GERMAN = "de";

  @XmlAttribute private String languageType = "authoritative";

  @XmlValue private String value = GERMAN;
}
