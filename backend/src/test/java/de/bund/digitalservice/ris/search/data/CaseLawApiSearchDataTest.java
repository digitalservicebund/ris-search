package de.bund.digitalservice.ris.search.data;

import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@SpringBootTest
@Tag("data")
@Disabled
class CaseLawApiSearchDataTest extends BaseApiSearchDataTest {

  private final int maxEntries = 500;

  private final double threshold = 95.0;

  private final String apiUrl = "/v1/case-law";

  @Autowired
  public CaseLawApiSearchDataTest(OAuth2AuthorizedClientManager authorizedClientManager) {
    super(authorizedClientManager);
  }

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = BASE_URL;
    RestAssured.defaultParser = Parser.JSON;
  }

  @Test
  void testCaseLawFileNumberSearchResults() {
    List<String> searchStrings =
        fetchSearchStrings(
            maxEntries,
            apiUrl,
            response -> {
              List<Map<String, Object>> members = response.path("member");
              return members.stream()
                  .map(
                      member ->
                          (List<String>)
                              ((Map<String, Object>) member.get("item")).get("fileNumbers"))
                  .flatMap(List::stream)
                  .toList();
            });
    List<String> successfulFileNumbers =
        searchForSearchTerm(this::searchForSingleFileNumber, searchStrings);
    double successRate = calculatePercentage(successfulFileNumbers.size(), searchStrings.size());
    assertThat(successRate, Matchers.greaterThan(threshold));
  }

  @Test
  void testCaseLawECLISearchResults() {
    List<String> searchStrings =
        fetchSearchStrings(
            maxEntries,
            apiUrl,
            response -> {
              List<Map<String, Object>> members = response.path("member");
              return members.stream()
                  .map(member -> (String) ((Map<String, Object>) member.get("item")).get("ecli"))
                  .filter(Objects::nonNull)
                  .toList();
            });
    List<String> successfulECLIS = searchForSearchTerm(this::searchForSingleECLI, searchStrings);
    double successRate = calculatePercentage(successfulECLIS.size(), searchStrings.size());
    assertThat(successRate, Matchers.greaterThan(threshold));
  }

  private Optional<String> searchForSingleFileNumber(String fileNumber) {
    try {
      Response response = searchWithTerm(fileNumber);
      List<Map<String, Object>> members = response.path("member");
      if (members == null || members.isEmpty()) return Optional.empty();
      Map<String, Object> firstMatch = members.getFirst();
      List<String> fileNumbers =
          (List<String>) ((Map<String, Object>) firstMatch.get("item")).get("fileNumbers");

      if (fileNumbers != null && fileNumbers.contains(fileNumber)) {
        return Optional.of(fileNumber);
      }
    } catch (Exception e) {
      System.out.println("Error searching for file number: " + fileNumber);
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private Optional<String> searchForSingleECLI(String ecli) {
    try {
      Response response = searchWithTerm(ecli);
      List<Map<String, Object>> members = response.path("member");
      if (members == null || members.isEmpty()) return Optional.empty();
      Map<String, Object> firstMatch = members.getFirst();
      String ecliFromMatch = (String) ((Map<String, Object>) firstMatch.get("item")).get("ecli");

      if (ecliFromMatch != null && ecliFromMatch.equals(ecli)) {
        return Optional.of(ecli);
      }
    } catch (Exception e) {
      System.out.println("Error occurred while searching for ECLI: " + ecli);
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
