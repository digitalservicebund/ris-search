package de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemapindex;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
@XmlType(name = "sitemap")
public class SitemapIndexEntry {

  private String loc;
}
