package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the navigation information used in a query.
 *
 * <p>This class encapsulates details such as the request identifier and paging information. It is
 * typically used within the context of a query to manage navigation-related metadata.
 *
 * <p>An instance of this class can associate a request ID and page details to describe specific
 * aspects of query navigation handling.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Navigation {
  @XmlElement(name = "request-id")
  private String requestId;

  private Page page;
}
