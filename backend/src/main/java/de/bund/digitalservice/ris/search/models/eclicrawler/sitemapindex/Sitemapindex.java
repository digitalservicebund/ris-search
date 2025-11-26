package de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the sitemap index structure as defined by the Sitemap Protocol.
 *
 * <p>This class corresponds to the root "sitemapindex" element in the Sitemap Protocol structure
 * (namespace: http://www.sitemaps.org/schemas/sitemap/0.9). It serves to aggregate multiple
 * sitemaps, allowing for their representation and handling in a collection format.
 *
 * <p>The `sitemaps` field contains the list of sitemap entries, represented by {@link
 * SitemapIndexEntry}, where each entry describes a specific sitemap within the index.
 *
 * <p>This class is annotated with JAXB annotations to enable serialization and deserialization of
 * XML data in compliance with the Sitemap Protocol structure. The namespace and element
 * configurations are defined to ensure compatibility with the protocol specification.
 */
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
