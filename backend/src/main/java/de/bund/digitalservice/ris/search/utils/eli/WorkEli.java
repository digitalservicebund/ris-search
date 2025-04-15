package de.bund.digitalservice.ris.search.utils.eli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record WorkEli(
    String jurisdiction, String agent, String year, String naturalIdentifier, String subtype) {

  public static WorkEli fromString(String workEli) {
    Matcher matcher =
        Pattern.compile(
                "eli/(?<jurisdiction>[^/]+)/(?<agent>[^/]+)/(?<year>[^/]+)/(?<naturalIdentifier>[^/]+)/(?<subtype>[^/]+)")
            .matcher(workEli);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid work Eli");
    }

    return new WorkEli(
        matcher.group("jurisdiction"),
        matcher.group("agent"),
        matcher.group("year"),
        matcher.group("naturalIdentifier"),
        matcher.group("subtype"));
  }

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s/%s".formatted(jurisdiction, agent, year, naturalIdentifier, subtype);
  }
}
