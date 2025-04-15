package de.bund.digitalservice.ris.search.utils.eli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ExpressionEli(
    String jurisdiction,
    String agent,
    String year,
    String naturalIdentifier,
    LocalDate pointInTime,
    Integer version,
    String language,
    String subtype) {

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s/%s/%d/%s/%s"
        .formatted(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            version,
            language,
            subtype);
  }

  /**
   * Example return:
   *
   * <pre>eli/bund/bgbl-1/1979/s1325/2020-06-19/2/deu</pre>
   *
   * Full expression ELI:
   *
   * <pre>eli/bund/bgbl-1/1979/s1325/2020-06-19/2/deu/regelungstext-1</pre>
   *
   * @return A string corresponding to the FRBRuri property of the underlying LegalDocML.de files.
   *     Unlike {@link #toString()}, this is a prefix of the manifestation ELI and can be used to
   *     pre-filter. Note that it is missing the subtype, which is required to uniquely identify
   *     documents.
   */
  public String getUriPrefix() {
    return "eli/%s/%s/%s/%s/%s/%d/%s"
        .formatted(
            jurisdiction,
            agent,
            year,
            naturalIdentifier,
            pointInTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
            version,
            language);
  }

  public WorkEli getWorkEli() {
    return new WorkEli(jurisdiction, agent, year, naturalIdentifier, subtype);
  }

  static final Pattern pattern =
      Pattern.compile(
          "eli/(?<jurisdiction>[^/]+)/(?<agent>[^/]+)/(?<year>[^/]+)/(?<naturalIdentifier>[^/]+)/(?<pointInTime>[^/]+)/(?<version>[^/]+)/(?<language>[^/]+)/(?<subtype>[^/]+)");

  public static ExpressionEli fromString(String expressionEli) {
    Matcher matcher = pattern.matcher(expressionEli);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid expression Eli");
    }

    return new ExpressionEli(
        matcher.group("jurisdiction"),
        matcher.group("agent"),
        matcher.group("year"),
        matcher.group("naturalIdentifier"),
        LocalDate.parse(matcher.group("pointInTime"), DateTimeFormatter.ISO_LOCAL_DATE),
        Integer.valueOf(matcher.group("version")),
        matcher.group("language"),
        matcher.group("subtype"));
  }
}
