package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.search.utils.LuceneQueryTools.validateLuceneQuery;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.mapper.MappingDefinitions;
import de.bund.digitalservice.ris.search.mapper.SortParamsConverter;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSortParam;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling export REST API. This class is annotated with {@link
 * RestController} and {@link RequestMapping} to define the base URL for handling export in the API.
 */
@Tag(name = "Export", description = "API endpoints to bulk export data.")
@RestController
@RequestMapping(ApiConfig.Paths.EXPORT_ADVANCED_SEARCH)
@ConditionalOnProperty(prefix = "feature-flags", value = "advanced-search", havingValue = "true")
public class ExportController {

  private final CaseLawService caseLawService;
  private final ExportService exportService;

  /**
   * Constructor for the ExportController class.
   *
   * @param caseLawService The {@link CaseLawService} to be used
   * @param exportService The {@link ExportService} to be used
   */
  @Autowired
  public ExportController(CaseLawService caseLawService, ExportService exportService) {
    this.caseLawService = caseLawService;
    this.exportService = exportService;
  }

  /**
   * Retrieves the CSV representation of a search query. The search query as query parameters.
   *
   * @param query The query filter based on Lucene query
   * @param pagination Pagination parameters
   * @param includedFields The fields to be included in the export
   * @param response The {@link HttpServletResponse} to be used to send the CSV as a response
   * @throws CustomValidationException If any request data information is wrong
   */
  @GetMapping(value = "query", produces = "text/csv")
  @Operation(
      summary = "Advanced search using Lucene query syntax (CSV)",
      description = "Advanced search using Lucene query syntax")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "500", description = "Internal Server Error")
  public void exportByQuery(
      @Parameter(name = "query", description = "The query filter based on Lucene query")
          @RequestParam(value = "query")
          String query,
      @ParameterObject @Valid PaginationParams pagination,
      @ParameterObject @Valid CaseLawSortParam sortParams,
      @Parameter(name = "fields", description = "The fields to be included in the export")
          @RequestParam(value = "field", defaultValue = "")
          List<String> includedFields,
      HttpServletResponse response)
      throws CustomValidationException {
    String decodedQuery = validateLuceneQuery(query);
    includedFields = validateExportFields(includedFields);

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd_HH-mm-ss"));
    String filename = "caselaw_export_" + timestamp + ".csv";

    PageRequest pageable = PageRequest.of(pagination.getPageIndex(), pagination.getSize());
    PageRequest sortedPageable =
        pageable.withSort(
            SortParamsConverter.buildSort(
                sortParams.getSort(), MappingDefinitions.ResolutionMode.CASE_LAW, true));

    List<CaseLawDocumentationUnit> results =
        caseLawService.searchCaseLaws(decodedQuery, sortedPageable).getContent().stream()
            .map(SearchHit::getContent)
            .toList();
    exportService.writeListAsCsvToResponse(filename, includedFields, results, response);
  }

  private static List<String> validateExportFields(List<String> includedFields)
      throws CustomValidationException {
    List<String> actualFieldNames =
        Arrays.stream(CaseLawSchema.class.getRecordComponents())
            .map(RecordComponent::getName)
            .toList();
    if (includedFields.isEmpty()) {
      return actualFieldNames;
    }
    List<String> includedFieldsCopy = List.copyOf(includedFields);
    includedFields.removeAll(actualFieldNames);

    if (!includedFields.isEmpty()) {
      var errors = new ArrayList<CustomError>();
      errors.add(
          CustomError.builder()
              .code("invalid_fields")
              .parameter("field")
              .message("Invalid fields to be included in the CSV export: " + includedFields)
              .build());
      throw new CustomValidationException(errors);
    }

    return includedFieldsCopy;
  }
}
