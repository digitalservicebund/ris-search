package de.bund.digitalservice.ris.search.sitemap.caselaw.schema;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement
@Data
@Accessors(chain = true)
public class UrlSet {

  @XmlElement(name = "url")
  private List<Url> url;
}
