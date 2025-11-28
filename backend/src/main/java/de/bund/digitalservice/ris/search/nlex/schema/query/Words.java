package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a word-based condition used in query configurations.
 *
 * <p>This class is part of the query structure, specifically utilized to define word-related
 * filtering criteria. It includes attributes and elements necessary for expressing conditions based
 * on words, such as the associated index name and the content to be matched.
 *
 * <p>Fields: - `idxName`: Represents the name of the index to which the word filtering condition
 * applies. - `contains`: Specifies the word or phrase that should be present in the filtering
 * logic.
 *
 * <p>This class is often used within higher-level query structures, such as criteria or boolean
 * conjunctions, to provide more detailed control over query conditions.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Words {

  @XmlAttribute(name = "idx-name")
  private String idxName;

  /** based on the configuration only containsType is allowed */
  @XmlElement private String contains;
}
