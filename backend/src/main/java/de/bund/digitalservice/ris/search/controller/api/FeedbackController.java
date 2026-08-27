package de.bund.digitalservice.ris.search.controller.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.service.PostHogService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling PostHog feedback through the backend API. This class is annotated
 * with {@link RestController} and {@link RequestMapping} to define the base URL for handling export
 * in the API.
 */
@Profile({"test", "prototype", "dev"})
@Tag(name = "Feedback", description = "API endpoints to send feedback to posthog.")
@RestController
@RequestMapping(ApiConfig.Paths.FEEDBACK)
public class FeedbackController {

  /** Request body record for feedback submissions. */
  public record FeedbackRequest(
      @NotNull String text,
      @NotNull String url,
      // The PostHog distinct ID of the user, or "anonymous_feedback_user" if not tracked.
      @NotNull @JsonProperty("user_id") String userId,
      // Honeypot field: if non-empty the submission is silently dropped to block bots.
      String name) {}

  private final PostHogService postHogService;

  /**
   * Constructor for the FeedbackController class.
   *
   * @param postHogService The {@link PostHogService} to be used
   */
  public FeedbackController(PostHogService postHogService) {
    this.postHogService = postHogService;
  }

  /**
   * Sends the user feedback to PostHog.
   *
   * @param body The feedback request body containing text, url, userId and an optional honeypot
   * @return ResponseEntity with message indicating success or failure even if a bot is used so that
   *     smart bots would not keep on trying or improving their tactics
   */
  @Hidden
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Sends the user feedback to PostHog",
      description = "Sends the user feedback to PostHog to avoid recording user data.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "422", description = "Required feedback data is missing")
  @ApiResponse(responseCode = "500", description = "Internal Server Error")
  public ResponseEntity<Map<String, String>> sendFeedback(
      @Valid @RequestBody FeedbackRequest body) {
    if (body.name() == null || body.name().isEmpty()) {
      postHogService.sendFeedback(body.userId(), body.url(), body.text());
    }

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("message", "Feedback sent successfully"));
  }
}
