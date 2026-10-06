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
 * The RateLimitFilter is an abstract class that provides a base implementation for rate-limiting
 * incoming HTTP requests based on the client IP address.
 *
 * <p>It leverages a caching mechanism to count and manage the number of requests from each client
 * IP within a specified time window. If the number of requests from a client exceeds the configured
 * maximum, the filter denies the request and responds with an HTTP 429 (Too Many Requests) status
 * code.
 *
 * <p>Only the initial request dispatch is counted. Async (e.g. StreamingResponseBody) and error
 * re-dispatches are skipped by {@link OncePerRequestFilter}.
 *
 * <p>Subclasses must provide implementation by specifying the maximum number of allowed requests
 * and the duration of the time window via the constructor.
 *
 * <p>Core functionalities include: - Tracking the number of requests made by each client IP. -
 * Enforcing expiration and cleanup of request records after a specified time window. - Returning a
 * 429 status code for clients exceeding the allowed request limit.
 *
 * <p>Designed to be extended for cases where specific rate-limiting configurations are required for
 * different endpoints or use cases.
 */
public abstract class RateLimitFilter extends OncePerRequestFilter {
  private final Cache<String, Integer> requestCountPerIpAddress;

  private final int maxRequests;

  protected RateLimitFilter(int maxRequests, int timeInSeconds) {

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
}
