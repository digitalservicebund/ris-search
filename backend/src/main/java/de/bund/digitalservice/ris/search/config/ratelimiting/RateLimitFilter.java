package de.bund.digitalservice.ris.search.config.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Rate-limits incoming HTTP requests based on the client IP address.
 *
 * <p>It leverages a caching mechanism to count and manage the number of requests from each client
 * IP within a specified time window. If the number of requests from a client exceeds the configured
 * maximum, the filter denies the request and responds with an HTTP 429 (Too Many Requests) status
 * code.
 *
 * <p>Only the initial request dispatch is counted. Async (e.g. StreamingResponseBody) and error
 * re-dispatches are skipped by {@link OncePerRequestFilter}. Actuator endpoints (health checks,
 * metrics scraping) are never limited.
 *
 * <p>The maximum number of requests and the time window are passed via the constructor. Separate
 * instances with their own limits and request counters can be registered for different URL
 * patterns, see {@code WebMvcConfig}.
 */
public class RateLimitFilter extends OncePerRequestFilter {
  private final Cache<String, Integer> requestCountPerIpAddress;
  private static final String ACTUATOR_PATH_PREFIX = "/actuator/";
  private final int maxRequests;

  /**
   * Creates a rate limit filter allowing a number of requests per client IP within a time window.
   *
   * @param maxRequests maximum number of requests allowed per client IP within the time window
   * @param timeInSeconds duration of the time window in seconds
   */
  public RateLimitFilter(int maxRequests, int timeInSeconds) {

    this.maxRequests = maxRequests;

    requestCountPerIpAddress =
        Caffeine.newBuilder()
            .expireAfter(
                new Expiry<String, Integer>() {
                  @Override
                  public long expireAfterCreate(
                      @NotNull String key, @NotNull Integer value, long currentTime) {
                    return TimeUnit.SECONDS.toNanos(timeInSeconds);
                  }

                  @Override
                  public long expireAfterUpdate(
                      @NotNull String key,
                      @NotNull Integer value,
                      long currentTime,
                      long currentDuration) {
                    return currentDuration;
                  }

                  @Override
                  public long expireAfterRead(
                      @NotNull String key,
                      @NotNull Integer value,
                      long currentTime,
                      long currentDuration) {
                    return currentDuration;
                  }
                })
            .build();
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

    String clientIpAddress = request.getRemoteAddr();
    Integer requestCount = requestCountPerIpAddress.getIfPresent(clientIpAddress);
    if (Objects.isNull(requestCount)) {
      requestCount = 0;
    }

    if (requestCount >= maxRequests) {
      response.sendError(429);
      return;
    }

    requestCountPerIpAddress.put(clientIpAddress, requestCount + 1);
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
    return request.getRequestURI().startsWith(ACTUATOR_PATH_PREFIX);
  }

  /** Resets cache state to empty. */
  void reset() {
    requestCountPerIpAddress.invalidateAll();
  }
}
