package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a single page configuration used in navigation details within queries.
 *
 * <p>This class is used to define page-related metadata in the context of query navigation. It
 * holds the page number as an attribute to indicate which page should be referenced or fetched
 * during a query operation.
 *
 * <p>The `number` attribute specifies the numerical identifier of the page.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Page {

  @XmlAttribute private int number;
}
