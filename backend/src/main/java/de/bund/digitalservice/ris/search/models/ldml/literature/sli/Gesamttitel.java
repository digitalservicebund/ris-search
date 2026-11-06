package de.bund.digitalservice.ris.search.models.ldml.literature.sli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
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
  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private String titel;

  @Nullable
  @XmlElement(name = "bandbezeichnung", namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private String bandbezeichnung;
}
