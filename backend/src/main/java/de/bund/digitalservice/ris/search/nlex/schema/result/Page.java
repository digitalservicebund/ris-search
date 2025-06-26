package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Page {

  @XmlAttribute private int number;

  @XmlAttribute private int size;
}
