package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an individual URL entry within a Sitemap XML structure. This class is used to define
 * the url element, containing details such as the location and an optional last modification date.
 *
 * <p>The class is annotated to support JAXB (Jakarta XML Binding) for XML serialization and
 * deserialization. The fields map to the respective XML elements: - `loc`: Specifies the URL
 * location. This field is required. - `lastmod`: Represents the last modification date of the URL.
 * This field is optional and uses a custom adapter for proper date formatting/parsing.
 *
 * <p>JAXB Annotations: - {@code @XmlElement}: Maps the fields to their respective
 */
@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Url {
  @XmlElement(name = "loc", required = true)
  private String loc;

  @XmlElement(name = "lastmod")
  @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
  private LocalDate lastmod;

  public Url(String loc) {
    this.loc = loc;
  }
}
