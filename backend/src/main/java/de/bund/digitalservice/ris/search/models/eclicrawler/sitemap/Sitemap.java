package de.bund.digitalservice.ris.search.models.eclicrawler.sitemap;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a Sitemap structure for XML serialization.
 *
 * <p>The Sitemap class is the root element for defining a collection of URLs included in a sitemap,
 * adhering to the associated XML schema. Each URL is represented by an instance of the {@code Url}
 * class.
 *
 * <p>This class leverages JAXB annotations to define XML element mappings: -
 * {@code @XmlRootElement}: Specifies the root element name as "urlset". - {@code @XmlElement}: Maps
 * the list of URLs to the "url" XML tag. - {@code @XmlTransient}: Excludes certain properties
 * (e.g., {@code name}) from XML serialization.
 *
 * <p>The Lombok annotations {@code @Data} and {@code @Accessors(chain = true)} are used to
 * automatically generate getter, setter, equals, hashCode, toString methods, and provide a
 * fluent-style setter approach.
 */
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
