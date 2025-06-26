package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Para {

  @XmlValue private String value;

  @XmlAttribute private String roles;
}
