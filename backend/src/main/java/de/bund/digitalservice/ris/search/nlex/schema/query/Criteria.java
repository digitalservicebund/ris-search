package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Criteria {

  @XmlAttribute(name = "encoding")
  private String criteriaEncoding;

  private Words words;

  /** based on our n-lex configuration only boolean and is allowed */
  private BooleanAnd and;
}
