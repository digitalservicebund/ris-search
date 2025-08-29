package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class FrbrNameValueElement {

  @XmlAttribute private String name;

  @XmlAttribute private String value;
}
