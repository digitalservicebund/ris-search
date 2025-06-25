package de.bund.digitalservice.ris.search.nlex.schema.query;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Navigation {
  @XmlElement(name = "request-id")
  private String requestId;

  private Page page;
}
