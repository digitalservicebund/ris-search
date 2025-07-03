package de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Identifier {
  public static final String LANG_DE = "de";
  public static final String FORMAT_HTML = "text/html";

  @XmlAttribute private String lang;
  @XmlAttribute private String format;

  @XmlValue private String value;
}
