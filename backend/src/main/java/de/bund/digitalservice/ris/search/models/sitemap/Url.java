package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
