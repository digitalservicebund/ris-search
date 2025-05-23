package de.bund.digitalservice.ris.search.models.api.parameters;

import static de.bund.digitalservice.ris.search.config.ApiConfig.DEFAULT_API_PAGE_SIZE;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationParams {
  @Parameter(
      name = "size",
      description = "The number of entities per page",
      example = DEFAULT_API_PAGE_SIZE)
  @Max(value = 100, message = "size must not exceed 100")
  @Min(value = 1, message = "size must be at least 1")
  int size = Integer.parseInt(DEFAULT_API_PAGE_SIZE);

  @Parameter(
      name = "pageIndex",
      description = "The number of the page to request. The page starts with the value 0",
      example = "0")
  int pageIndex = 0;

  @Schema(
      description =
          "The field to sort the results by. Default is the relevance score calculated by OpenSearch. Valid usage of the sort field are : date (sort by date ascending), -date (sort by date descending) and not setting the sort field (sort by relevance descending).")
  String sort = null;
}
