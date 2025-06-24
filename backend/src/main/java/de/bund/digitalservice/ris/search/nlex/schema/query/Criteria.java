package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Criteria {

  @XmlAttribute(name = "encoding")
  private String criteriaEncoding;

  private Words words;

  /** based on our n-lex configuration only boolean and is allowed */
  private BooleanAnd and;
}
