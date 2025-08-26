package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class FrbrAuthor {

  @XmlAttribute(name = "as")
  private String as;

  @XmlAttribute(name = "href")
  private String href;
}
