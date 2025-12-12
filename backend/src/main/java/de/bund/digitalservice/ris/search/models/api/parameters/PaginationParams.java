package de.bund.digitalservice.ris.search.models.api.parameters;

import static de.bund.digitalservice.ris.search.config.ApiConfig.DEFAULT_API_PAGE_SIZE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Represents the parameters used for pagination in API requests.
 *
 * <p>The class includes settings to control the number of entities returned per page and the
 * requested page index. Additionally, it enforces limits to ensure that the result window does not
 * exceed the maximum permissible value.
 *
 * <p>Attributes: - size: The number of entities per page, with constraints ensuring it falls within
 * a defined range (1 to 100). - pageIndex: The index of the page to retrieve, starting at 0.
 *
 * <p>An internal validation is present to ensure the calculated result window (size * pageIndex +
 * size) does not exceed 10,000.
 */
@Data
public class PaginationParams {
  @Parameter(
      name = "size",
      description = "The number of entities per page",
      example = DEFAULT_API_PAGE_SIZE)
  @Max(value = 300, message = "size must not exceed 300")
  @Min(value = 1, message = "size must be at least 1")
  int size = Integer.parseInt(DEFAULT_API_PAGE_SIZE);

  @Parameter(
      name = "pageIndex",
      description = "The number of the page to request. The page starts with the value 0",
      example = "0")
  @Min(value = 0, message = "pageIndex must be at least 0")
  int pageIndex = 0;

  @AssertTrue(message = "PageIndex out of Bounds. Result window must not exceed 10000")
  @JsonIgnore
  private boolean isResultWindowValid() {
    return (size * pageIndex + size) <= 10000;
  }
}
