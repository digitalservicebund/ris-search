package de.bund.digitalservice.ris.search.config.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

public abstract class RateLimitInterceptor implements HandlerInterceptor {
  private final Cache<String, Integer> requestCountPerIpAddress;

  private final int maxRequests;

  protected RateLimitInterceptor(int maxRequests, int timeInSeconds) {

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
  public boolean preHandle(
      HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
      throws IOException {

    String clientIpAddress = request.getRemoteAddr();
    Integer requestCount = requestCountPerIpAddress.getIfPresent(clientIpAddress);
    if (Objects.isNull(requestCount)) {
      requestCount = 1;
    }
    if (requestCount > maxRequests) {
      response.sendError(429);
      return false;
    }
    requestCountPerIpAddress.put(clientIpAddress, ++requestCount);
    return true;
  }
}
