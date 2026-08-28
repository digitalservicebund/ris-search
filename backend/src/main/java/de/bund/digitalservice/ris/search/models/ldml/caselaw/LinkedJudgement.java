package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import de.bund.digitalservice.ris.search.utils.DateUtils;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

/** Represents a linked court decision (judgement) and its associated metadata. */
@Getter
@Setter
public class LinkedJudgement {
  private static final String PENDING_PROCEEDING_ART = "anhängig";

  @XmlAttribute(name = "art")
  private String art;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentType;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String decisionDate;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fileNumber;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht risGericht;

  /**
   * Returns a simplified string representation containing the file number and court type.
   *
   * @return a comma-separated string of the judgement details
   */
  public String asString() {
    String courtType = (risGericht != null) ? risGericht.getGerichtstyp() : null;

    return Stream.of(fileNumber, courtType)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(", "));
  }

  /**
   * Formats this ensuing decision (nachgehende Entscheidung) as "Gericht, Typ vom Datum -
   * Aktenzeichen", appending " (anhängig)" if it is a pending proceeding (i.e. its {@code art}
   * attribute is "anhängig").
   *
   * @return the formatted ensuing decision, or {@code null} if none of its parts are present
   */
  public String getEnsuingDecisionFormatted() {
    String gerichtstyp = (risGericht != null) ? risGericht.getGerichtstyp() : null;
    String formatiertesDatum =
        DateUtils.toGermanLongDateString(DateUtils.nullSafeParseyyyyMMdd(decisionDate));

    StringBuilder result =
        new StringBuilder(
            Stream.of(gerichtstyp, documentType)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", ")));

    if (formatiertesDatum != null) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append("vom ").append(formatiertesDatum);
    }

    if (fileNumber != null) {
      if (!result.isEmpty()) {
        result.append(" - ");
      }
      result.append(fileNumber);
    }

    if (result.isEmpty()) {
      return null;
    }
    if (PENDING_PROCEEDING_ART.equalsIgnoreCase(art)) {
      result.append(" (anhängig)");
    }
    return result.toString();
  }
}
