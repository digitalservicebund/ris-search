package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.models.api.parameters.NormExpressionId;
import de.bund.digitalservice.ris.search.models.api.parameters.NormWorkId;
import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSearchSchema;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for handling REST API requests for legislation. */
@RestController
@Tag(
    name = "Legislation",
    description =
        """
        Retrieve current and historical versions of laws and decrees.
        The endpoints operate on the levels of "work", "expression", and "manifestation". See
        <a href="https://en.wikipedia.org/wiki/Functional_Requirements_for_Bibliographic_Records">Functional
        Requirements for Bibliographic Records (FRBR)</a> for more information.
        """)
public interface NormsController {
  @Operation(
      summary = "Retrieves expression level metadata for a given work eli",
      description = "Returns all legislation items based on its work eli")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  @Hidden
  CollectionSchema<LegislationExpressionSearchSchema> getWorkExamples(
      @ModelAttribute NormWorkId normWorkId, @ParameterObject @Valid PaginationParams pagination);

  @Operation(
      summary = "Work and expression-level metadata",
      description = "Returns metadata of a legislation item.")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", content = @Content)
  ResponseEntity<LegislationExpressionSchema> getLegislation(
      @ParameterObject @ModelAttribute NormExpressionId norm);
}
