package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder = {"format", "display", "href"})
public class ExternUrl {

  @XmlAttribute private String href;

  @XmlAttribute private final String format = "text/html";

  @XmlAttribute private final String display = "HTML extern";
}
