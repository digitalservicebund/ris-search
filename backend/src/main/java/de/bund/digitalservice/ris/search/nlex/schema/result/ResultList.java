package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a list of results in the application's search schema.
 *
 * <p>The {@code ResultList} class is a container for navigation metadata and a list of documents.
 * It includes information about the overall navigation of search results and the individual
 * documents being returned as part of the result set.
 *
 * <p>Fields: - {@code navigation}: Encapsulates the navigation metadata associated with the result
 * list, represented by the {@code Navigation} class. This includes pagination details, requests,
 * and hit counts. - {@code documents}: A list of {@code Document} objects, where each document
 * represents a single search result with its own content and reference metadata.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields directly to XML elements. - {@code @XmlElementWrapper}: Groups the list of documents under
 * a wrapping element when serialized to XML. - {@code @XmlElement}: Defines the representation of
 * individual document elements in the XML structure. - Lombok annotations {@code @Data} and
 * {@code @Accessors(chain = true)}: Automatically generate boilerplate methods like getters and
 * setters and enable method chaining.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class ResultList {

  private Navigation navigation;

  @XmlElementWrapper
  @XmlElement(name = "document")
  private List<Document> documents;
}
