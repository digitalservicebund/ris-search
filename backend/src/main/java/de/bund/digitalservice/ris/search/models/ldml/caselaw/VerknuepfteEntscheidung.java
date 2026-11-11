package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// NOTE: This only exists for a transition period.
// The main functionality (the formatting function) this interface holds
// will be moved to the frontend.
/** A decision linked to the documented decision, either preceding or ensuing it. */
public interface VerknuepfteEntscheidung {

  String getReferenzUri();

  String getDokumenttyp();

  String getEntscheidungsdatum();

  String getDokumentnummer();

  String getAktenzeichen();

  RisGericht getGericht();

  default boolean isAnhaengig() {
    return false;
  }

  /**
   * Formats this decision as "Gerichtstyp Gerichtsort, Typ vom Datum - Aktenzeichen", appending "
   * (anhängig)" if it is a pending proceeding.
   *
   * @return the formatted decision, or {@code null} if none of its parts are present
   */
  default String getFormatted() {
    RisGericht gericht = getGericht();
    String gerichtFormatted =
        gericht == null
            ? ""
            : Stream.of(gericht.getGerichtstyp(), gericht.getGerichtsort())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    String formatiertesDatum =
        DateUtils.toGermanLongDateString(DateUtils.nullSafeParseyyyyMMdd(getEntscheidungsdatum()));

    StringBuilder result =
        new StringBuilder(
            Stream.of(gerichtFormatted.isBlank() ? null : gerichtFormatted, getDokumenttyp())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", ")));

    if (formatiertesDatum != null) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append("vom ").append(formatiertesDatum);
    }

    if (getAktenzeichen() != null) {
      if (!result.isEmpty()) {
        result.append(" - ");
      }
      result.append(getAktenzeichen());
    }

    if (result.isEmpty()) {
      return null;
    }
    if (isAnhaengig()) {
      result.append(" (anhängig)");
    }
    return result.toString();
  }
}
