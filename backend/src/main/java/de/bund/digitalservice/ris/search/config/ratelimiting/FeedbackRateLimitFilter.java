package de.bund.digitalservice.ris.search.config.ratelimiting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The FeedbackRateLimitFilter class extends the {@link RateLimitFilter} to provide rate-limiting
 * functionality specifically for feedback-related HTTP requests.
 *
 * <p>This filter enforces a limit on the number of feedback requests made by a single client IP
 * address within a specified time window. Requests exceeding the configured limit will receive an
 * HTTP 429 (Too Many Requests) response.
 *
 * <p>Configuration for this rate limiter is provided via external properties: -
 * "rate-limit.feedback.requests": The maximum allowed number of feedback requests. -
 * "rate-limit.feedback.seconds": The time window duration in seconds within which requests are
 * counted.
 *
 * <p>The rate-limiting mechanism is achieved using a caching solution that tracks request counts
 * per client IP and enforces expiration based on the configured time window.
 *
 * <p>This class is annotated with {@code @Component}, marking it as a Spring-managed bean. It is
 * restricted to the feedback endpoint by its registration in {@code WebMvcConfig}.
 */
@Component
public class FeedbackRateLimitFilter extends RateLimitFilter {

  public FeedbackRateLimitFilter(
      @Value("${rate-limit.feedback.requests}") int maxRequests,
      @Value("${rate-limit.feedback.seconds}") int seconds) {
    super(maxRequests, seconds);
  }
}
