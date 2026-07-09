package de.bund.digitalservice.ris.search.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/** Utility class for expanding court name abbreviations using a predefined set of synonyms. */
@Component
public class CourtNameAbbreviationExpander {

  private static final Map<String, String> synonyms;

  private CourtNameAbbreviationExpander() {}

  static {
    Map<String, String> synonymMap = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    try (InputStream inputStream =
        CourtNameAbbreviationExpander.class
            .getClassLoader()
            .getResourceAsStream("openSearch/german_analyzer_template.json")) {

      if (inputStream == null) {
        throw new IllegalStateException("JSON template file not found on classpath");
      }

      JsonNode rootNode = objectMapper.readTree(inputStream);
      JsonNode synonymsArray =
          rootNode
              .path("template")
              .path("settings")
              .path("analysis")
              .path("filter")
              .path("court_type_synonyms")
              .path("synonyms");

      if (synonymsArray.isArray()) {
        for (JsonNode node : synonymsArray) {
          String line = node.asText();
          String[] parts = line.split(",");

          if (parts.length > 1) {
            String fullNameValue = parts[parts.length - 1].trim();
            for (int i = 0; i < parts.length - 1; i++) {
              String abbreviationKey = parts[i].trim();
              synonymMap.put(abbreviationKey, fullNameValue);
            }
          }
        }
      }

      synonyms = Collections.unmodifiableMap(synonymMap);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to initialize court synonyms from JSON template", e);
    }
  }

  /**
   * Extracts the first token from the given prefix string and converts it to uppercase.
   *
   * @param prefix The input string from which to extract the first token.
   * @return The first token in uppercase, or null if the input is null or empty.
   */
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

  /**
   * Expands court name abbreviations in the given key using the loaded synonyms.
   *
   * @param key The input string containing court name abbreviations.
   * @param keepToken A token prefix to keep unchanged (can be null).
   * @return The input string with court name abbreviations expanded.
   */
  public static @NotNull String getLabelExpandingSynonyms(String key, String keepToken) {
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
}
