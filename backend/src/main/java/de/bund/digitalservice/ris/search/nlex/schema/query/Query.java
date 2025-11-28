package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a query object used within the system.
 *
 * <p>This class is a container for various elements of a query, including navigation details,
 * search criteria, and output-related configurations such as language and format. It is designed to
 * support flexible and structured query definitions.
 *
 * <p>Fields: - `navigation`: Encapsulates navigation-related metadata such as request ID and paging
 * information. - `criteria`: Represents the search criteria specifying how queries should be
 * executed or filtered. - `outputLang`: Specifies the desired output language as an XML attribute.
 * - `outputFormat`: Defines the output format of the query result as an XML attribute.
 */
@XmlRootElement(name = "request")
@Data
@Accessors(chain = true)
@XmlType(propOrder = {})
public class Query {
  private Navigation navigation;

  private Criteria criteria;

  @XmlAttribute(name = "output_lang")
  private String outputLang;

  @XmlAttribute(name = "output_format")
  private String outputFormat;
}
