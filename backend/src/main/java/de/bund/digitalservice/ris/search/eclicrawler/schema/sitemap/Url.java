package de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap;

import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Document;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Url {
  private String loc;

  @XmlElement(namespace = "https://e-justice.europa.eu/ecli")
  private Document document;
}
