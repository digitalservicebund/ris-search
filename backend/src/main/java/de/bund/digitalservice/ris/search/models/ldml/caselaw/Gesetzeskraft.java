package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/** Represents the binding legal force (Gesetzeskraft) ruling on a specific Einzelnorm. */
@Getter
@Setter
public class Gesetzeskraft {

  @XmlElement(name = "gesetzeskraftTyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String gesetzeskraftTyp;

  @XmlElement(name = "geltungsbereich", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String geltungsbereich;

  /**
   * Formats this legal force ruling as the verdict followed by the jurisdiction it applies to, e.g.
   * "vereinbar mit höherrangigem Recht, Bremen".
   *
   * @return the formatted ruling, or {@code null} if no verdict is present
   */
  public String getFormatted() {
    if (gesetzeskraftTyp == null || gesetzeskraftTyp.isBlank()) {
      return null;
    }
    if (geltungsbereich == null || geltungsbereich.isBlank()) {
      return gesetzeskraftTyp.trim();
    }
    return gesetzeskraftTyp.trim() + ", " + geltungsbereich.trim();
  }
}
