package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement()
@Data
public class Request {
  private Navigation navigation;

  private Criteria criteria;

  @XmlAttribute(name = "output_lang")
  private String outputLang;

  @XmlAttribute(name = "output_format")
  private String outputFormat;
}
