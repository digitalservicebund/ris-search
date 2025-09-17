package de.bund.digitalservice.ris.search;

import de.bund.digitalservice.ris.search.util.OAuthTokenProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

public abstract class BaseApiSearchDataTest {

  public static final String BASE_URL = "http://localhost:8090";

  private final OAuthTokenProvider tokenProvider;

  public BaseApiSearchDataTest(OAuth2AuthorizedClientManager authorizedClientManager) {
    this.tokenProvider = new OAuthTokenProvider(authorizedClientManager);
  }

  public <T> List<T> fetchSearchStrings(
      int maxEntries, String apiUrl, Function<Response, List<T>> extractor) {
    List<T> results = new ArrayList<>();
    String url = buildInitialUrl(apiUrl);

    while (results.size() < maxEntries) {
      Response response = fetchPageResponse(url);
      List<T> extracted = extractor.apply(response);
      results.addAll(extracted);

      url = getNextUrl(response);
      if (url == null) break;
    }

    return results;
  }

  public Response searchWithTerm(String searchTerm) {
    return RestAssured.given()
        .header("Authorization", "Bearer " + this.tokenProvider.getTokenValue())
        .when()
        .get("/v1/document?searchTerm=" + searchTerm)
        .then()
        .extract()
        .response();
  }

  public <T> List<T> searchForSearchTerm(
      Function<T, Optional<T>> searchFunction, List<T> searchStrings) {
    return searchStrings.parallelStream()
        .map(searchFunction)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public static double calculatePercentage(int part, int total) {
    if (total == 0) {
      return 0.0;
    }
    return ((double) part / total) * 100;
  }

  private String buildInitialUrl(String apiUrl) {
    int page = 0;
    int pageSize = 100;
    return String.format("%s?pageIndex=%d&size=%d", apiUrl, page, pageSize);
  }

  private String getNextUrl(Response response) {
    Map<String, Object> view = response.path("view");
    return (String) view.get("next");
  }

  Response fetchPageResponse(String url) {
    return RestAssured.given()
        .header("Authorization", "Bearer " + this.tokenProvider.getTokenValue())
        .when()
        .get(url)
        .then()
        .extract()
        .response();
  }
}
