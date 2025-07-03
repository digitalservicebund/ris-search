package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlRootElement(name = "request")
@Data
@Accessors(chain = true)
@XmlType(propOrder = {"navigation", "criteria"})
public class Query {
  private Navigation navigation;

  private Criteria criteria;

  @XmlAttribute(name = "output_lang")
  private String outputLang;

  @XmlAttribute(name = "output_format")
  private String outputFormat;
}
