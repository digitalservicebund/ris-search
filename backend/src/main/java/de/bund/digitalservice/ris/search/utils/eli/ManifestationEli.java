package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ManifestationEli(
    String jurisdiction,
    String agent,
    String year,
    String naturalIdentifier,
    LocalDate pointInTime,
    Integer version,
    String language,
    LocalDate pointInTimeManifestation,
    String subtype) {

  @Override
  public String toString() {
    return toStringWithoutFileExtension() + ".xml";
  }

  public String toStringWithoutFileExtension() {
    return "eli/%s/%s/%s/%s/%s/%d/%s/%s/%s"
        .formatted(
            jurisdiction(),
            agent(),
            year(),
            naturalIdentifier(),
            pointInTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
            version(),
            language(),
            pointInTimeManifestation().format(DateTimeFormatter.ISO_LOCAL_DATE),
            subtype());
  }

  public WorkEli getWorkEli() {
    return new WorkEli(jurisdiction(), agent(), year(), naturalIdentifier(), subtype());
  }

  public ExpressionEli getExpressionEli() {
    return new ExpressionEli(
        jurisdiction(),
        agent(),
        year(),
        naturalIdentifier(),
        pointInTime(),
        version(),
        language(),
        subtype());
  }

  static final Pattern pattern =
      Pattern.compile(
          "eli/(?<jurisdiction>[^/]+)/(?<agent>[^/]+)/(?<year>[^/]+)/(?<naturalIdentifier>[^/]+)/(?<pointInTime>[^/]+)/(?<version>[^/]+)/(?<language>[^/]+)/(?<pointInTimeManifestation>[^/]+)/(?<subtype>[^/]+)\\.xml");

  public static Optional<ManifestationEli> fromString(String manifestationEli) {
    Matcher matcher = pattern.matcher(manifestationEli);

    if (!matcher.matches()) {
      return Optional.empty();
    }
    try {

      return Optional.of(
          new ManifestationEli(
              matcher.group("jurisdiction"),
              matcher.group("agent"),
              matcher.group("year"),
              matcher.group("naturalIdentifier"),
              LocalDate.parse(matcher.group("pointInTime"), DateTimeFormatter.ISO_LOCAL_DATE),
              Integer.valueOf(matcher.group("version")),
              matcher.group("language"),
              LocalDate.parse(
                  matcher.group("pointInTimeManifestation"), DateTimeFormatter.ISO_LOCAL_DATE),
              matcher.group("subtype")));
    } catch (IllegalStateException | IllegalArgumentException | DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
