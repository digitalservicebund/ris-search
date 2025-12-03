package de.bund.digitalservice.ris.search.models.ldml.literature.sil;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class FRBRNumber {

  @XmlAttribute String value;
}
