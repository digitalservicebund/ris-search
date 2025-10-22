package de.bund.digitalservice.ris.search.config.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.web.servlet.HandlerInterceptor;

public abstract class RateLimitInterceptor implements HandlerInterceptor {
  Cache<String, Integer> requestCountsPerIpAddress =
      Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
  private final int maxRequestsPerMinute;

  public RateLimitInterceptor(int maxRequestsPerMinute) {
    this.maxRequestsPerMinute = maxRequestsPerMinute;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {

    String clientIpAddress = request.getRemoteAddr();

    Integer requestCount = requestCountsPerIpAddress.getIfPresent(clientIpAddress);
    if (Objects.isNull(requestCount)) {
      requestCount = 1;
    }

    if (requestCount > maxRequestsPerMinute) {
      response.sendError(429);
      return false;
    }
    requestCountsPerIpAddress.put(clientIpAddress, ++requestCount);
    return true;
  }
}
