package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents a citation (Fundstelle) of a case law document within a periodical. */
@Getter
@Setter
public class Fundstelle {

  @XmlElement(name = "periodikum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Periodikum periodikum;

  @XmlElement(name = "zitatstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String zitatstelle;

  @XmlElement(name = "fundstellenTyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fundstellenTyp;

  /**
   * Formats this citation the way German case law citations are usually displayed, i.e. the
   * periodical's abbreviation followed by the citation location (e.g. "BGHSt 67, 273-284").
   *
   * @return the formatted citation, or {@code null} if the abbreviation or citation location is
   *     missing
   */
  public String getFormatted() {
    String abkuerzung = periodikum != null ? periodikum.getAbkuerzung() : null;
    if (abkuerzung == null || zitatstelle == null) {
      return null;
    }
    return abkuerzung.trim() + " " + zitatstelle.trim();
  }
}
