package de.bund.digitalservice.ris.search.config.ratelimiting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultRateLimitInterceptor extends RateLimitInterceptor {

  public DefaultRateLimitInterceptor(
      @Value("${rate-limit.default.requests}") int maxRequests,
      @Value("${rate-limit.default.seconds}") int seconds) {
    super(maxRequests, seconds);
  }
}
