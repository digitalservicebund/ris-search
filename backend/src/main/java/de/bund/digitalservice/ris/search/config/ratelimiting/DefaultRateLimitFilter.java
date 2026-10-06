package de.bund.digitalservice.ris.search.config.ratelimiting;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A default implementation of the {@link RateLimitFilter} that is responsible for rate-limiting
 * HTTP requests based on configurable thresholds.
 *
 * <p>This filter limits the number of requests from a single client IP address to a configurable
 * maximum within a specific time window. Actuator endpoints (health checks, metrics scraping) are
 * excluded.
 *
 * <p>The configuration for this rate limiter is provided via external properties: -
 * "rate-limit.default.requests": The maximum number of requests allowed. -
 * "rate-limit.default.seconds": The duration of the time window, in seconds, during which requests
 * are counted.
 *
 * <p>The filter works by leveraging a caching mechanism to store and manage the number of requests
 * made by each client IP within the specified time window. Requests exceeding the defined limit
 * return an HTTP 429 (Too Many Requests) response.
 *
 * <p>This class is annotated with {@code @Component}, marking it as a Spring-managed bean.
 */
@Component
public class DefaultRateLimitFilter extends RateLimitFilter {

  private static final String ACTUATOR_PATH_PREFIX = "/actuator/";

  public DefaultRateLimitFilter(
      @Value("${rate-limit.default.requests}") int maxRequests,
      @Value("${rate-limit.default.seconds}") int seconds) {
    super(maxRequests, seconds);
  }

  @Override
  protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
    return request.getRequestURI().startsWith(ACTUATOR_PATH_PREFIX);
  }
}
