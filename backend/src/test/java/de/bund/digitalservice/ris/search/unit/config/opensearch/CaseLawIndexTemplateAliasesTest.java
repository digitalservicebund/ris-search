package de.bund.digitalservice.ris.search.unit.config.opensearch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Verifies that every advanced-search alias defined in {@code case_law_index_template.json} is
 * reachable in both an uppercase and a lowercase form, without needing a live OpenSearch instance
 * or firing HTTP requests for each one (that coverage lives in a small representative set of cases
 * in {@code AdvancedSearchControllerApiTest} instead).
 */
class CaseLawIndexTemplateAliasesTest {

  private static final String TEMPLATE_PATH = "openSearch/case_law_index_template.json";

  @Test
  void everyAliasHasBothAnUppercaseAndLowercaseForm() throws IOException {
    JsonNode properties = loadTemplateProperties();
    List<String> aliases = findAliasesMissingACaseCounterpart(properties);
    assertThat(aliases).as("aliases missing a case counterpart").isEmpty();
  }

  private JsonNode loadTemplateProperties() throws IOException {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
      assertThat(stream).as("template resource %s must exist", TEMPLATE_PATH).isNotNull();
      JsonNode root = new ObjectMapper().readTree(stream);
      return root.at("/template/mappings/properties");
    }
  }

  private List<String> findAliasesMissingACaseCounterpart(JsonNode properties) {
    List<String> aliases = new ArrayList<>();
    Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      checkAliasHasCounterpart(entry.getKey(), entry.getValue(), properties)
          .ifPresent(aliases::add);
    }
    return aliases;
  }

  /**
   * Checks a single property: if it's an alias with a case-sensitive name, its opposite-case
   * counterpart must exist and point at the same underlying field.
   *
   * @return a description of the problem, or empty if the property is fine (or not an alias we need
   *     to check)
   */
  private Optional<String> checkAliasHasCounterpart(
      String key, JsonNode value, JsonNode properties) {
    if (!isAlias(value)) {
      return Optional.empty();
    }
    String counterpart = oppositeCase(key);
    if (counterpart == null) {
      return Optional.empty();
    }

    String path = pathOf(value);
    JsonNode counterpartValue = properties.get(counterpart);
    if (counterpartValue == null) {
      return Optional.of(missingCounterpartMessage(key, path, counterpart));
    }
    return checkCounterpartPointsToSameField(key, path, counterpart, counterpartValue);
  }

  /**
   * The counterpart property must resolve to the same underlying field as the original alias,
   * either by being an alias with a matching {@code path}, or by being the real field itself (e.g.
   * {@code "ECLI"} points at path {@code "ecli"}, and {@code "ecli"} is the actual field, not an
   * alias).
   */
  private Optional<String> checkCounterpartPointsToSameField(
      String key, String path, String counterpart, JsonNode counterpartValue) {
    if (isAlias(counterpartValue)) {
      String counterpartPath = pathOf(counterpartValue);
      if (!path.equals(counterpartPath)) {
        return Optional.of(differentPathMessage(key, path, counterpart, counterpartPath));
      }
      return Optional.empty();
    }
    if (!counterpart.equals(path)) {
      return Optional.of(notTheRealFieldMessage(key, path, counterpart));
    }
    return Optional.empty();
  }

  private boolean isAlias(JsonNode value) {
    return "alias".equals(value.path("type").asText(null));
  }

  private String pathOf(JsonNode value) {
    return value.path("path").asText(null);
  }

  /**
   * For a case-only key (e.g. an alias abbreviation), returns the opposite-case form, or {@code
   * null} if the key isn't case-sensitive (e.g. contains no letters, or is mixed case).
   */
  private static String oppositeCase(String key) {
    if (key.equals(key.toUpperCase()) && !key.equals(key.toLowerCase())) {
      return key.toLowerCase();
    }
    if (key.equals(key.toLowerCase()) && !key.equals(key.toUpperCase())) {
      return key.toUpperCase();
    }
    return null;
  }

  private static String missingCounterpartMessage(String key, String path, String counterpart) {
    return "'%s' (-> %s) has no counterpart '%s'".formatted(key, path, counterpart);
  }

  private static String differentPathMessage(
      String key, String path, String counterpart, String counterpartPath) {
    return "'%s' -> '%s', but '%s' -> '%s' (paths differ)"
        .formatted(key, path, counterpart, counterpartPath);
  }

  private static String notTheRealFieldMessage(String key, String path, String counterpart) {
    return "'%s' -> '%s', but counterpart '%s' is a real field with a different name"
        .formatted(key, path, counterpart);
  }
}
