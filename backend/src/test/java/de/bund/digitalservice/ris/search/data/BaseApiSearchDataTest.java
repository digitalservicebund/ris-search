package de.bund.digitalservice.ris.search.data;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for API search data tests, providing methods to fetch and search data from a paginated
 * API endpoint.
 */
public abstract class BaseApiSearchDataTest {

  public static final String BASE_URL = "http://localhost:8090";

  /**
   * Fetches search strings from a paginated API endpoint until the maximum number of entries is
   * reached or there are no more pages.
   *
   * @param maxEntries the maximum number of entries to fetch
   * @param apiUrl the base URL of the API endpoint
   * @param extractor a function to extract the desired data from the API response
   * @param <T> the type of the data to be fetched
   * @return a list of fetched data
   */
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
    return given().when().get("/v1/document?searchTerm=" + searchTerm).then().extract().response();
  }

  /**
   * Searches for multiple terms in parallel using the provided search function.
   *
   * @param searchFunction a function that takes a search term and returns an Optional result
   * @param searchStrings a list of search terms to be searched
   * @param <T> the type of the search result
   * @return a list of found results
   */
  public <T> List<T> searchForSearchTerm(
      Function<T, Optional<T>> searchFunction, List<T> searchStrings) {
    return searchStrings.parallelStream()
        .map(searchFunction)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  /**
   * Calculates the percentage of part over total.
   *
   * @param part the part value
   * @param total the total value
   * @return the percentage of part over total
   */
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
    return given().when().get(url).then().extract().response();
  }
}
