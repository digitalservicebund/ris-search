package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a complete European Legislation Identifier (ELI) for a specific legislative file. The
 * EliFile record aggregates metadata about a legislative document, from the jurisdictional context
 * to the specific digital manifestation of the file.
 *
 * <p>The class is built upon the structural hierarchy of ELI, encompassing WorkEli, ExpressionEli,
 * and ManifestationEli. It includes parsing mechanisms to construct the structure from an ELI
 * string and methods to generate different levels of ELI representations.
 *
 * <p>Fields: - jurisdiction: Specifies the jurisdiction where the legislative document applies. -
 * agent: Denotes the agent responsible for creating the document. - year: Represents the year
 * associated with the legislative document's publication or creation. - naturalIdentifier: Encodes
 * a unique identifier for the document in the context of its jurisdiction and agent. - pointInTime:
 * Specifies the moment in time relevant to the legislative expression. - version: Indicates the
 * version of the legislative document. - language: Establishes the language of the document. -
 * pointInTimeManifestation: Denotes the point in time related to the manifestation of the document.
 * - fileName: Represents the name of the file containing the legislative document. - format:
 * Describes the file format of the legislative document (e.g., "pdf", "html").
 *
 * <p>Main Functionalities: - Parsing a structured ELI string to an EliFile object using regular
 * expressions. - Providing WorkEli, ExpressionEli, and ManifestationEli representations derived
 * from the complete ELI structure. - Formatting the complete ELI instance as a standardized string.
 *
 * <p>Methods: - fromString(String fileName): Parses the provided ELI string and returns an optional
 * EliFile object if the string matches the expected pattern and contains valid metadata. -
 * getWorkEli(): Generates and retrieves the WorkEli representation from the instance. -
 * getExpressionEli(): Creates and retrieves the ExpressionEli representation from the instance. -
 * getManifestationEli(): Constructs and retrieves the ManifestationEli representation for the
 * instance. - toString(): Outputs the string representation of the instance using its
 * ManifestationEli format.
 */
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

  /**
   * Parses the given file name into an {@code EliFile} object if the file name matches the expected
   * pattern. If the file name does not match or any parsing errors occur, an empty {@code Optional}
   * is returned.
   *
   * @param fileName the ELI file name to be parsed
   * @return an {@code Optional} containing the parsed {@code EliFile}, or an empty {@code Optional}
   *     if the file name is invalid
   */
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
