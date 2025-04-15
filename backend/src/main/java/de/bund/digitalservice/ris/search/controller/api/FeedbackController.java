package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.service.PostHogService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling PostHog feedback through the backend API. This class is annotated
 * with {@link RestController} and {@link RequestMapping} to define the base URL for handling export
 * in the API.
 */
@Tag(name = "Feedback", description = "API endpoints to send feedback to posthog.")
@RestController
@RequestMapping(ApiConfig.Paths.FEEDBACK)
public class FeedbackController {

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
   * @param text The content of the feedback to be sent
   * @param url The URL to be sent
   * @param userId The user identifier for PostHog. If the user is not opted for tracking then this
   *     field should be "anonymous_feedback"
   * @param phHost The PostHog host.
   * @param phKey The PostHog API key.
   * @param phSurveyId The PostHog survey ID.
   * @return ResponseEntity with message indicating success or failure
   */
  @Hidden
  @GetMapping
  @Operation(
      summary = "Sends the user feedback to PostHog",
      description = "Sends the user feedback to PostHog to avoid recording user data.")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "422", description = "Required feedback data is missing")
  @ApiResponse(responseCode = "500", description = "Internal Server Error")
  public ResponseEntity<Map<String, String>> sendFeedback(
      @Parameter(name = "text", description = "The feedback text to be sent")
          @RequestParam(value = "text")
          String text,
      @Parameter(name = "url", description = "The URL to be sent") @RequestParam(value = "url")
          String url,
      @Parameter(name = "user_id", description = "The user identifier to be sent")
          @RequestParam(value = "user_id")
          String userId,
      @Parameter(name = "ph_host", description = "The PostHog host")
          @RequestParam(value = "ph_host")
          String phHost,
      @Parameter(name = "ph_key", description = "The PostHog API key")
          @RequestParam(value = "ph_key")
          String phKey,
      @Parameter(name = "pg_survey_id", description = "The PostHog survey ID")
          @RequestParam(value = "ph_survey_id")
          String phSurveyId) {
    try {
      postHogService.sendFeedback(userId, url, text, phHost, phKey, phSurveyId);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("message", "Feedback sent successfully"));
    } catch (Exception exception) {
      throw new ErrorResponseException(HttpStatusCode.valueOf(500), exception.getCause());
    }
  }
}
