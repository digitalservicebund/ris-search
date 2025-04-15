package de.bund.digitalservice.ris.search.service.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class CourtNameAbbreviationExpander {
  private final Map<String, String> synonyms;

  public CourtNameAbbreviationExpander() throws IOException {
    this.synonyms = loadCourtSynonyms();
  }

  public static String extractFirstToken(String prefix) {
    if (prefix == null) {
      return null;
    }
    var parts = StringUtils.split(prefix, " ");
    if (parts.length > 0) {
      return parts[0].toUpperCase();
    } else {
      return null;
    }
  }

  public @NotNull String getLabelExpandingSynonyms(String key, String keepToken) {
    return Arrays.stream(key.split(" "))
        .map(
            part -> {
              if (keepToken != null && part.startsWith(keepToken)) {
                return part;
              }
              String synonym = synonyms.get(part);
              return synonym != null ? synonym : part;
            })
        .collect(Collectors.joining(" "));
  }

  private static Map<String, String> loadCourtSynonyms() throws IOException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    InputStream is = classloader.getResourceAsStream("openSearch/mounted/court_synonyms.txt");
    assert is != null;
    InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);

    var result = new LinkedHashMap<String, String>();
    for (String line; (line = reader.readLine()) != null; ) {
      if (line.startsWith("#") || line.isBlank()) continue;
      var terms = line.split(", ");
      if (terms.length >= 2) {
        var key = terms[0];
        var value = terms[terms.length - 1];
        result.put(key, value);
      }
    }
    return result;
  }
}
