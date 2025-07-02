package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

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
