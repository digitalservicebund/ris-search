package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * Represents a full title if additonaly given to the main title.
 *
 * <p>Fields: - titel: name of the titel. - bandbezeichnung: quoted location
 */
@Getter
public class Gesamttitel {
  @XmlAttribute String titel;

  @Nullable @XmlAttribute String bandbezeichnung;
}
