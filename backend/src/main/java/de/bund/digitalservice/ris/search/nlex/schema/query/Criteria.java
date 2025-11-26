package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the search criteria used in a query.
 *
 * <p>This class holds specific conditions and attributes related to how queries should be executed
 * or filtered. It is designed to support the configuration of criteria elements, allowing the
 * definition of encoding type, specific word-based conditions, and boolean conjunctions.
 *
 * <p>The `criteriaEncoding` attribute specifies the encoding format related to the query's
 * criteria. The `words` and `and` fields are used to define specific matching words or boolean
 * combinations for more complex query logic.
 */
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
