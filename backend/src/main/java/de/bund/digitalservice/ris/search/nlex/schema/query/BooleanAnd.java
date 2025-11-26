package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a boolean "AND" operation within a query.
 *
 * <p>This class defines a logical conjunction for query criteria, specifically allowing the
 * combination of words-based conditions. It is part of the configuration of search criteria,
 * supporting advanced logic for filtering or matching specific query elements.
 *
 * <p>The `words` field allows the specification of words-related conditions for the "AND"
 * operation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class BooleanAnd {

  private Words words;
}
