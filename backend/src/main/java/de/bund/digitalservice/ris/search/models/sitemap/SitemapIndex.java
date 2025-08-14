package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "sitemapindex")
@XmlAccessorType(XmlAccessType.FIELD)
public class SitemapIndex {
  @XmlElement(name = "url")
  private List<Url> urls;
}
