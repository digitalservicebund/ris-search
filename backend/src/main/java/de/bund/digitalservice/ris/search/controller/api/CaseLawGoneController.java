package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import de.bund.digitalservice.ris.search.models.errors.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Responds to the removed {@code /v1/case-law/**} endpoints with {@code 410 Gone}, pointing callers
 * to their {@code /v1/rechtsprechung/**} equivalent instead of a plain {@code 404}.
 *
 * @deprecated these endpoints no longer exist; use {@link RechtsprechungController} instead. Kept
 *     only so old clients get a meaningful error instead of a plain {@code 404}.
 */
@Deprecated(since = "2026-10-06", forRemoval = true)
@RestController
@Tag(name = "Rechtsprechung")
@Profile({"dev", "e2e", "default", "staging", "uat", "test", "prototype"})
public class CaseLawGoneController {

  private static final String OLD_BASE = "/v1/case-law";

  /**
   * Handles any removed {@code /v1/case-law/**} request, responding with {@code 410 Gone} and the
   * equivalent {@code /v1/rechtsprechung/**} path to use instead.
   *
   * @param request the incoming request, used to determine the removed path and query string
   * @return a {@link CustomErrorResponse} pointing to the new endpoint
   */
  @GetMapping({OLD_BASE, OLD_BASE + "/**"})
  @ResponseStatus(HttpStatus.GONE)
  @Operation(
      deprecated = true,
      summary = "Removed: use the rechtsprechung endpoint instead",
      description =
          "All case-law endpoints have been removed. Please use the equivalent /v1/rechtsprechung endpoint instead.")
  @ApiResponse(responseCode = "410", description = "Gone - this endpoint has been removed")
  public ResponseEntity<CustomErrorResponse> handleRemovedEndpoint(HttpServletRequest request) {
    String suffix = request.getRequestURI().substring(OLD_BASE.length());
    String newPath =
        UriComponentsBuilder.fromPath(ApiConfig.Paths.RECHTSPRECHUNG + suffix)
            .query(request.getQueryString())
            .build()
            .toUriString();

    CustomError error =
        new CustomError(
            HttpStatus.GONE.name().toLowerCase(),
            "This endpoint has been removed. Use " + newPath + " instead.",
            newPath);

    // Old clients may still send Accept: text/html. Presetting the content type skips content
    // negotiation, which would otherwise fail with a 406 instead of returning the 410.
    return ResponseEntity.status(HttpStatus.GONE)
        .contentType(MediaType.APPLICATION_JSON)
        .body(CustomErrorResponse.builder().errors(List.of(error)).build());
  }
}
