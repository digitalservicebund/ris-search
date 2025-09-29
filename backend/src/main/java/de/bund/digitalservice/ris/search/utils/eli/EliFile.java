package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record EliFile(
    String jurisdiction,
    String agent,
    String year,
    String naturalIdentifier,
    LocalDate pointInTime,
    Integer version,
    String language,
    LocalDate pointInTimeManifestation,
    String fileName,
    String format) {

  static final Pattern pattern =
      Pattern.compile(
          "eli/(?<jurisdiction>[^/]+)/(?<agent>[^/]+)/(?<year>[^/]+)/(?<naturalIdentifier>[^/]+)/(?<pointInTime>[^/]+)/(?<version>[^/]+)/(?<language>[^/]+)/(?<pointInTimeManifestation>[^/]+)/(?<fileName>[^/]+)\\.(?<format>[^/]+)");

  public static Optional<EliFile> fromString(String fileName) {
    Matcher matcher = pattern.matcher(fileName);

    if (!matcher.matches()) {
      return Optional.empty();
    }
    try {

      return Optional.of(
          new EliFile(
              matcher.group("jurisdiction"),
              matcher.group("agent"),
              matcher.group("year"),
              matcher.group("naturalIdentifier"),
              LocalDate.parse(matcher.group("pointInTime"), DateTimeFormatter.ISO_LOCAL_DATE),
              Integer.valueOf(matcher.group("version")),
              matcher.group("language"),
              LocalDate.parse(
                  matcher.group("pointInTimeManifestation"), DateTimeFormatter.ISO_LOCAL_DATE),
              matcher.group("fileName"),
              matcher.group("format")));
    } catch (IllegalStateException | IllegalArgumentException | DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public WorkEli getWorkEli() {
    return new WorkEli(jurisdiction(), agent(), year(), naturalIdentifier());
  }

  public ExpressionEli getExpressionEli() {
    return new ExpressionEli(
        jurisdiction(), agent(), year(), naturalIdentifier(), pointInTime(), version(), language());
  }

  public ManifestationEli getManifestationEli() {
    return new ManifestationEli(
        jurisdiction(),
        agent(),
        year(),
        naturalIdentifier(),
        pointInTime(),
        version(),
        language(),
        pointInTimeManifestation(),
        fileName(),
        format());
  }

  @Override
  public String toString() {
    return getManifestationEli().toString();
  }
}
