package de.bund.digitalservice.ris.search.data;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@SpringBootTest
@Tag("data")
class CaseLawFileNumberTest extends BaseDataTest {

  private List<String> fileNumbers;

  private double threshold = 95;

  private int maxEntries = 500;

  @Autowired
  public CaseLawFileNumberTest(OAuth2AuthorizedClientManager authorizedClientManager) {
    super(authorizedClientManager);
    this.fileNumbers = fetchFileNumbers();
  }

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = "http://localhost:8090";
    RestAssured.defaultParser = Parser.JSON;
  }

  @Test
  void testCaselawFilenameSearchResults() {
    List<String> successfulFileNumbers = searchForFileNumbers();
    double successRate = calculatePercentage(successfulFileNumbers.size(), fileNumbers.size());
    assertThat(successRate, Matchers.greaterThan(threshold));
  }

  private List<String> fetchFileNumbers() {

    int page = 0;
    String url = "/v1/case-law?pageIndex=" + page + "&size=100";

    List<String> fileNumbersFromApi = new ArrayList<>();

    while (fileNumbersFromApi.size() < maxEntries) {

      Response response =
          given()
              .header("Authorization", "Bearer " + getAccessToken())
              .when()
              .get(url)
              .then()
              .extract()
              .response();

      List<Map<String, Object>> members = response.path("member");

      List<String> pageResult =
          members.stream()
              .map(
                  member ->
                      (List<String>) ((Map<String, Object>) member.get("item")).get("fileNumbers"))
              .flatMap(List::stream)
              .toList();
      fileNumbersFromApi.addAll(pageResult);

      LinkedHashMap view = response.path("view");
      String next = (String) view.get("next");
      if ((view.get("next") == null)) {
        break;
      } else {
        url = next;
      }
    }

    return fileNumbersFromApi;
  }

  private List<String> searchForFileNumbers() {

    return fileNumbers.parallelStream()
        .filter(
            fileNumber -> {
              try {
                return !this.searchForSingleFileNumber(fileNumber).isEmpty();
              } catch (Exception e) {
                return false;
              }
            })
        .toList();
  }

  private Optional<String> searchForSingleFileNumber(String fileNumber) {
    Response response =
        given()
            .header("Authorization", "Bearer " + getAccessToken())
            .when()
            .get("/v1/document?searchTerm=" + fileNumber)
            .then()
            .extract()
            .response();

    List<Map<String, Object>> members = response.path("member");
    Map<String, Object> firstMatch = members.get(0);
    List<String> fileNumbersOfFirstMatch =
        (List<String>) ((Map<String, Object>) firstMatch.get("item")).get("fileNumbers");
    if (fileNumbersOfFirstMatch != null && fileNumbersOfFirstMatch.contains(fileNumber)) {
      return Optional.of(fileNumber);
    } else {
      return Optional.empty();
    }
  }
}
