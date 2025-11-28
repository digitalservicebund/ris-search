package de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents an individual sitemap entry within a sitemap index.
 *
 * <p>This class corresponds to the "sitemap" element of the Sitemap Index defined in the Sitemap
 * Protocol specification (namespace: http://www.sitemaps.org/schemas/sitemap/0.9). It includes
 * details about the location (URL) of a specific sitemap.
 *
 * <p>The class is designed for use with XML binding annotations for serialization and
 * deserialization of XML data. The JAXB annotations ensure compatibility with the Sitemap Protocol
 * structure.
 *
 * <p>An instance of this class is typically part of a {@link Sitemapindex}, where it forms one
 * entry in the collection of sitemaps.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
@XmlType(name = "sitemap")
public class SitemapIndexEntry {

  private String loc;
}
