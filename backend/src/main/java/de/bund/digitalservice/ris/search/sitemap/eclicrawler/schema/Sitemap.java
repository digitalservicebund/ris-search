package de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement
@Data
@Accessors(chain = true)
@XmlType(name = "urlset")
public class Sitemap {

  @XmlElement(name = "url")
  private List<Url> url;
}
