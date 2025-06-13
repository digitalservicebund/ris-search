package de.bund.digitalservice.ris.search.nlex.service.schema;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result", namespace = "de.bund.digitalservice.ris.search")
public class Result {
  @XmlAttribute public String status;
  @XmlAttribute public String site;
  @XmlAttribute public String connector;
}
