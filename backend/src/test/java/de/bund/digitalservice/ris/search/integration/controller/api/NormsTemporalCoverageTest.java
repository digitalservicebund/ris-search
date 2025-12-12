package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class NormsTemporalCoverageTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  final List<Norm> allNorms =
      List.of(
          Norm.builder().expressionEli("1-5").entryIntoForceDate(day(1)).expiryDate(day(5)).build(),
          Norm.builder().expressionEli("3-5").entryIntoForceDate(day(3)).expiryDate(day(5)).build(),
          Norm.builder().expressionEli("1-2").entryIntoForceDate(day(1)).expiryDate(day(2)).build(),
          Norm.builder().expressionEli("3-3").entryIntoForceDate(day(3)).expiryDate(day(3)).build(),
          Norm.builder().expressionEli("4-5").entryIntoForceDate(day(4)).expiryDate(day(5)).build(),
          Norm.builder().expressionEli("-1").expiryDate(day(1)).build(),
          Norm.builder().expressionEli("-3").expiryDate(day(3)).build(),
          Norm.builder().expressionEli("-5").expiryDate(day(5)).build(),
          Norm.builder().expressionEli("1-").entryIntoForceDate(day(1)).build(),
          Norm.builder().expressionEli("3-").entryIntoForceDate(day(3)).build(),
          Norm.builder().expressionEli("5-").entryIntoForceDate(day(5)).build());

  @BeforeEach
  void setUpSearchControllerApiTest() {
    clearRepositoryData();
    normsRepository.saveAll(allNorms);
  }

  @Test
  @DisplayName("Returns all norms if no parameters are specified")
  void shouldReturnEverythingByDefault() throws Exception {

    mockMvc
        .perform(get(ApiConfig.Paths.LEGISLATION).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), resultHasItemCount(allNorms.size()));
  }

  @Test
  @DisplayName("Returns all norms that are valid on day 3")
  void shouldReturnItemsForSingleDay() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION
                    + "?temporalCoverageFrom=%s&temporalCoverageTo=%s".formatted(day(3), day(3)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), resultHasItems("1-5", "3-5", "3-3", "-3", "-5", "1-", "3-"));
  }

  @Test
  @DisplayName("Returns all norms that are valid between days 2 and 4")
  void shouldReturnItemsForRange() throws Exception {
    final Set<String> notExpectedIds = Set.of("-1", "5-");
    final String[] expected =
        allNorms.stream()
            .map(Norm::getExpressionEli)
            .filter(e -> !notExpectedIds.contains(e))
            .toArray(String[]::new);
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION
                    + "?temporalCoverageFrom=%s&temporalCoverageTo=%s".formatted(day(2), day(4)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(), resultHasItems(expected), resultHasItemCount(expected.length));
  }

  @ParameterizedTest
  @DisplayName("Returns correct norms for open-ended ranges")
  @CsvSource({
    "temporalCoverageTo, 4, 5-",
    "temporalCoverageFrom, 2, -1",
  })
  void shouldReturnItemsForRangeTo(String key, int day, String notExpectedId) throws Exception {
    final String[] expected =
        allNorms.stream()
            .map(Norm::getExpressionEli)
            .filter(e -> !Objects.equals(e, notExpectedId))
            .toArray(String[]::new);
    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION + "?%s=%s".formatted(key, day(day)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(), resultHasItems(expected), resultHasItemCount(expected.length));
  }

  @DisplayName("Rejects inverted ranges")
  @ParameterizedTest
  @ValueSource(strings = {ApiConfig.Paths.LEGISLATION, ApiConfig.Paths.DOCUMENT})
  void shouldRejectInvalidRange(String basePath) throws Exception {
    mockMvc
        .perform(
            get(basePath
                    + "?temporalCoverageFrom=%s&temporalCoverageTo=%s".formatted(day(4), day(2)))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().is4xxClientError(),
            jsonPath("$.errors[0].code", is("invalid_range")),
            jsonPath("$.errors[0].parameter", is("temporalCoverageFrom")));
  }

  private LocalDate day(int d) {
    return LocalDate.of(2000, 1, d);
  }

  private static @NotNull ResultMatcher resultHasItemCount(int size) {
    return jsonPath("$.totalItems", is(size));
  }

  private static @NotNull ResultMatcher resultHasItems(String... elis) {
    return jsonPath("$.member[*].item.workExample.legislationIdentifier", containsInAnyOrder(elis));
  }
}
