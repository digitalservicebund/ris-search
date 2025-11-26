package de.bund.digitalservice.ris.search.models.eclicrawler.sitemap;

import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Document;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents an individual URL entry in a sitemap. This class is part of the XML serialization
 * structure for sitemaps.
 *
 * <p>The URL includes a location and an associated document, providing additional metadata for the
 * resource referenced by the URL.
 *
 * <p>The {@code XmlAccessorType} annotation specifies that fields are serialized and deserialized
 * directly. The {@code Data} annotation from Lombok generates getter and setter methods, among
 * other features. The {@code Accessors(chain = true)} annotation enables fluent-style method
 * invocation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Url {
  private String loc;

  @XmlElement(namespace = "https://e-justice.europa.eu/ecli")
  private Document document;
}
