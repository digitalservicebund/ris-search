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
 * Represents a Sitemap XML file which conforms to the sitemap.org protocol. This class is used to
 * serialize and deserialize the sitemap XML structure, specifically the element containing multiple
 * entries.
 *
 * <p>Each entry in the sitemap is represented by the {@link Url} class, storing details like the
 * URL location and the last modification date.
 *
 * <p>The class is annotated to support JAXB (Jakarta XML Binding) for XML serialization and
 * deserialization. The root element of the XML is `urlset`, and each URL entry is represented as a
 * child `url` element.
 *
 * <p>JAXB Annotations: - {@code @XmlRootElement}: Maps the class to the root XML element `urlset`.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "urlset")
@XmlAccessorType(XmlAccessType.FIELD)
public class SitemapFile {
  @XmlElement(name = "url")
  private List<Url> urls;
}
