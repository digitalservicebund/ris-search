package de.bund.digitalservice.ris.search.models.eclicrawler.sitemap;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement(name = "urlset")
@Data
@Accessors(chain = true)
@XmlType(name = "urlset")
public class Sitemap {

  @XmlElement(name = "url")
  private List<Url> url;

  private String name;

  @XmlTransient
  public String getName() {
    return name;
  }
}
