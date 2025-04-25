package de.bund.digitalservice.ris.search.data;

import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
      Map<String, Object> firstMatch = members.get(0);
      String abbreviationFromMatch =
          (String) ((Map<String, Object>) firstMatch.get("item")).get("abbreviation");

      if (abbreviationFromMatch != null && abbreviationFromMatch.equals(abbreviation)) {
        return Optional.of(abbreviation);
      }
    } catch (Exception e) {
      System.out.println("Error occurred while searching for abbreviation: " + abbreviation);
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
