package de.bund.digitalservice.ris.search.data;

import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@SpringBootTest
@Tag("data")
class NormsApiSearchDataTest extends BaseApiSearchDataTest {

  private static final Logger logger = LogManager.getLogger(NormsApiSearchDataTest.class);

  private int maxEntries = 1000;

  private double threshold = 95.0;

  private String apiUrl = "/v1/legislation";

  @Autowired
  public NormsApiSearchDataTest(OAuth2AuthorizedClientManager authorizedClientManager) {
    super(authorizedClientManager);
  }

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = BASE_URL;
    RestAssured.defaultParser = Parser.JSON;
  }

  @Test
  void testLegislationAbbreviationSearchResults() {
    List<String> searchStrings =
        fetchSearchStrings(
            maxEntries,
            apiUrl,
            response -> {
              List<Map<String, Object>> members = response.path("member");
              return members.stream()
                  .map(
                      member ->
                          (String) ((Map<String, Object>) member.get("item")).get("abbreviation"))
                  .filter(abbreviation -> abbreviation != null && !abbreviation.isEmpty())
                  .toList();
            });
    Set<String> set = new HashSet<>(searchStrings);
    List<String> uniqueSearchStrings = new ArrayList<>(set);
    List<String> successfulAbbreviations =
        searchForSearchTerm(this::searchForSingleAbbreviation, uniqueSearchStrings);
    double successRate =
        calculatePercentage(successfulAbbreviations.size(), uniqueSearchStrings.size());
    assertThat(successRate, Matchers.greaterThan(threshold));
  }

  private Optional<String> searchForSingleAbbreviation(String abbreviation) {
    try {
      Response response = searchWithTerm(abbreviation);
      List<Map<String, Object>> members = response.path("member");
      if (members == null || members.isEmpty()) return Optional.empty();
      Map<String, Object> firstMatch = members.getFirst();
      String abbreviationFromMatch =
          (String) ((Map<String, Object>) firstMatch.get("item")).get("abbreviation");

      if (abbreviationFromMatch != null && abbreviationFromMatch.equals(abbreviation)) {
        return Optional.of(abbreviation);
      }
    } catch (Exception e) {
      logger.error("Error occurred while searching for abbreviation: {}", abbreviation, e);
    }
    return Optional.empty();
  }

  @Test
  void searchForArticleNumberAndAbbreviation() {
    List<ArticleNumberAndAbbreviation> searchCases =
        fetchSearchStrings(maxEntries, apiUrl, this::extractArticleNumbersAndAbbreviations);

    var set = new HashSet<>(searchCases);

    List<ArticleNumberAndAbbreviation> successfulAbbreviations =
        searchForSearchTerm(
            this::searchForSingleArticleNumberAndAbbreviation, set.stream().toList());
    double successRate = calculatePercentage(successfulAbbreviations.size(), set.size());

    successfulAbbreviations.forEach(set::remove);
    logger.info("successful rate: {}", successRate);
    logger.info("failed cases sample: {}", set.stream().limit(20).toList());
    assertThat(successRate, Matchers.greaterThan(threshold));
  }

  private List<ArticleNumberAndAbbreviation> extractArticleNumbersAndAbbreviations(
      Response response) {
    List<Map<String, Object>> members = response.path("member");
    final Stream<Map<String, Object>> itemsWithAbbreviation =
        members.stream()
            .filter(
                member -> {
                  String abbreviation =
                      (String) ((Map<String, Object>) member.get("item")).get("abbreviation");
                  return StringUtils.isNotBlank(abbreviation);
                });

    return itemsWithAbbreviation
        .flatMap(
            member -> {
              String abbreviation =
                  (String) ((Map<String, Object>) member.get("item")).get("abbreviation");
              String detailUrl =
                  ((Map<String, Object>)
                          ((Map<String, Object>) member.get("item")).get("workExample"))
                      .get("@id")
                      .toString();
              Response detailResponse = NormsApiSearchDataTest.this.fetchPageResponse(detailUrl);
              var articleNames = (List<String>) detailResponse.path("workExample.hasPart.name");
              Pattern pattern = Pattern.compile("§ \\d+");
              return articleNames.stream()
                  .map(
                      name -> {
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find()) {
                          return new ArticleNumberAndAbbreviation(abbreviation, matcher.group());
                        } else {
                          return null;
                        }
                      })
                  .filter(Objects::nonNull)
                  .limit(10);
            })
        .toList();
  }

  private Optional<ArticleNumberAndAbbreviation> searchForSingleArticleNumberAndAbbreviation(
      ArticleNumberAndAbbreviation testCase) {
    String searchTerm = testCase.articleNumber + " " + testCase.abbreviation;
    try {
      Response response = searchWithTerm(searchTerm);
      List<Map<String, Object>> members = response.path("member");
      if (members == null || members.isEmpty()) return Optional.empty();
      Map<String, Object> firstMatch = members.getFirst();
      String abbreviationFromMatch =
          (String) ((Map<String, Object>) firstMatch.get("item")).get("abbreviation");

      if (Objects.equals(abbreviationFromMatch, testCase.abbreviation)) {
        final var textMatches = (List<Map<String, String>>) firstMatch.get("textMatches");
        if (!textMatches.isEmpty()) {
          String name = textMatches.getFirst().get("name");
          if (name.startsWith(testCase.articleNumber)) {
            return Optional.of(testCase);
          } else {
            logger.info("did not match article number: {}, was: {}", testCase.articleNumber, name);
          }
        }
      } else {
        logger.info("did not match searchTerm: {}, {}", searchTerm, abbreviationFromMatch);
      }
    } catch (Exception e) {
      logger.error("Error occurred while searching for searchTerm: {}", searchTerm, e);
    }
    return Optional.empty();
  }

  record ArticleNumberAndAbbreviation(String abbreviation, String articleNumber) {}
}
