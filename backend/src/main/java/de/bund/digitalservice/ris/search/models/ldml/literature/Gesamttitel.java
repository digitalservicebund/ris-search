package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * Represents a full title if additonaly given to the main title.
 *
 * <p>Fields: - titel: name of the titel. - bandbezeichnung: quoted location
 */
@Getter
public class Gesamttitel {
  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_NS)
  private String titel;

  @Nullable
  @XmlElement(name = "bandbezeichnung", namespace = LiteratureNamespaces.RIS_NS)
  private String bandbezeichnung;
}
