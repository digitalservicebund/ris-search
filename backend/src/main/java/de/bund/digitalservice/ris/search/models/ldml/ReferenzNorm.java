package de.bund.digitalservice.ris.search.models.ldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/** Represents a reference to a norm (law), including its individually cited provisions. */
@Getter
@Setter
public class ReferenzNorm {

  @XmlElement(name = "abkuerzung", namespace = Namespaces.RIS_NS)
  private String abkuerzung;

  @XmlElement(name = "titel", namespace = Namespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "einzelnorm", namespace = Namespaces.RIS_NS)
  private List<Einzelnorm> einzelnorm;

  /**
   * Formats this reference as the abbreviation followed by the first Einzelnorm's designation and
   * version date, e.g. "GG, Art 6 Abs 2 S 1, 1949-05-23". Missing parts are left out.
   *
   * @return the formatted reference, or {@code null} if the abbreviation is missing
   */
  public String getFormatted() {
    if (StringUtils.isBlank(abkuerzung)) {
      return null;
    }
    if (einzelnorm == null || einzelnorm.isEmpty()) {
      return abkuerzung;
    }
    Einzelnorm first = einzelnorm.getFirst();
    return Stream.of(abkuerzung, first.getBezeichnung(), first.getFassungsdatum())
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(", "));
  }
}
