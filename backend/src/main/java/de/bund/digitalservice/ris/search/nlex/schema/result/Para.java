package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Para {

  @XmlValue private String value;

  @XmlAttribute private String roles;
}
