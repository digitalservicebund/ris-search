package de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement
@Data
@Accessors(chain = true)
public class Sitemapindex {

  @XmlElement(name = "sitemap")
  private List<SitemapIndexEntry> sitemaps;
}
