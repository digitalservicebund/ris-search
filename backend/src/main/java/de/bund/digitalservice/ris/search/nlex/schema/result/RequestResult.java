package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the result of a request in the application's search schema.
 *
 * <p>The {@code RequestResult} class serves as a container for the status of a request, site,
 * connector information, a list of search results, and any errors that may have occurred during
 * processing. This class is serialized using JAXB annotations for structured XML representation.
 *
 * <p>Fields: - {@code status}:
 */
@XmlRootElement(name = "result")
@Data
@Accessors(chain = true)
public class RequestResult {

  @XmlAttribute(name = "status")
  private String status;

  @XmlAttribute private String site;

  @XmlAttribute private String connector;

  @XmlElement(name = "result-list")
  private ResultList resultList;

  @XmlElementWrapper
  @XmlElement(name = "error")
  private List<Error> errors;
}
