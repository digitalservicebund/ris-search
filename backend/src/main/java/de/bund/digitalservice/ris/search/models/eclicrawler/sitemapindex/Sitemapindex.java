package de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement
@Data
@Accessors(chain = true)
public class Sitemapindex {

  @XmlElement(name = "sitemap")
  private List<SitemapIndexEntry> sitemaps;

  private String name;

  @XmlTransient
  public String getName() {
    return name;
  }
}
