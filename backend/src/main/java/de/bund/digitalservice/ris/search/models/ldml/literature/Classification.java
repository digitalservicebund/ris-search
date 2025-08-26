package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class Classification {

  @XmlAttribute private String source;

  @XmlElement(name = "keyword", namespace = LiteratureLdml.AKN_NS)
  private List<Keyword> keywords;
}
