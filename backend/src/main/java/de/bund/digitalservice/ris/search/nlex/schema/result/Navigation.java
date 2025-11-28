package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents navigation metadata, including request identification, pagination information, and hit
 * counts.
 *
 * <p>The {@code Navigation} class provides essential metadata for navigation and pagination
 * purposes in search results. This class encapsulates information such as a unique request ID,
 * pagination details, and the total number of hits for a search operation. It is utilized to
 * provide structured metadata for search operations within the system.
 *
 * <p>Fields: - {@code requestId}: A unique identifier associated with the search request. - {@code
 * page}: An instance of the {@code Page} class, which specifies pagination details (e.g., current
 * page number and size of each page). - {@code hits}: The total number of results (or "hits")
 * retrieved for the search query.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields of this class directly to XML elements or attributes. - {@code @XmlElement}: Maps specific
 * fields as XML elements in the serialized data. - Lombok annotations {@code @Data} and
 * {@code @Accessors(chain = true)}: Auto-generates boilerplate code such as getters, setters, and
 * equals/hashCode methods while enabling method chaining for a fluent API style.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Navigation {

  @XmlElement(name = "request-id")
  private String requestId;

  private Page page;

  @XmlElement private Long hits;
}
