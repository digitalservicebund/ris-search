package de.bund.digitalservice.ris.search.config.ratelimiting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.servlet.HandlerInterceptor;

public abstract class RateLimitInterceptor implements HandlerInterceptor {
  private final Map<String, AtomicInteger> requestCountsPerIpAddress = new ConcurrentHashMap<>();
  private final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();
  private final int maxRequestsPerMinute;
  private static final long TIME_LIMIT = 60000;

  public RateLimitInterceptor(int maxRequestsPerMinute) {
    this.maxRequestsPerMinute = maxRequestsPerMinute;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {

    String clientIpAddress = request.getRemoteAddr();
    long currentTime = System.currentTimeMillis();

    AtomicInteger requestCount =
        requestCountsPerIpAddress.computeIfAbsent(clientIpAddress, k -> new AtomicInteger(0));
    long firstRequestTime = requestTimestamps.computeIfAbsent(clientIpAddress, k -> currentTime);

    long timeElapsed = calculateTimeElapsed(firstRequestTime, currentTime);

    // Reset if more than 1 minute has passed
    if (timeElapsed > TIME_LIMIT) {
      requestCount.set(0);
      requestTimestamps.put(clientIpAddress, currentTime);
    }

    int requests = requestCount.incrementAndGet();
    if (requests > maxRequestsPerMinute) {
      response.sendError(429);
      return false;
    }

    return true;
  }

  private long calculateTimeElapsed(long firstRequestTime, long currentTime) {
    return Duration.between(
            Instant.ofEpochMilli(firstRequestTime), Instant.ofEpochMilli(currentTime))
        .toMillis();
  }
}
