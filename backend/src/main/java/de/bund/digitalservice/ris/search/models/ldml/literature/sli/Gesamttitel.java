package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class Gesamttitel {
  @XmlAttribute String titel;

  @Nullable @XmlAttribute String bandbezeichnung;
}
