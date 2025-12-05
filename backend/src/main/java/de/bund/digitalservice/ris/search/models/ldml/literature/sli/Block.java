package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;

@Getter
public class Block {
  @XmlValue String value;
}
