package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Sitemap index document that conforms to the sitemap.org protocol. This class is used
 * to serialize and deserialize the sitemap index XML structure, specifically the sitemapindex
 * element containing multiple url entries.
 *
 * <p>Each entry in the sitemap index is represented by the {@link Url} class, storing details such
 * as the URL location and the last modification date.
 *
 * <p>The class is annotated with JAXB (Jakarta XML Binding) annotations to facilitate XML
 * serialization and deserialization. The root element of the XML is `sitemapindex`, and each entry
 * is represented as a child `url` element.
 *
 * <p>JAXB Annotations: - {@code @XmlRootElement}: Maps the class to the root XML element
 * `sitemapindex`. - {@code @XmlElement}: Maps the collection of {@link Url} objects to the `url`
 * elements in XML. - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that fields are
 * directly mapped to XML elements without requiring getter or setter methods.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "sitemapindex")
@XmlAccessorType(XmlAccessType.FIELD)
public class SitemapIndex {
  @XmlElement(name = "url")
  private List<Url> urls;
}
