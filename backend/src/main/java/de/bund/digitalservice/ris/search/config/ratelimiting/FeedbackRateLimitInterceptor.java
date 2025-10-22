package de.bund.digitalservice.ris.search.config.ratelimiting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRateLimitInterceptor extends RateLimitInterceptor {

  public FeedbackRateLimitInterceptor(
      @Value("${rate-limit.feedback.rpm}") int maxRequestsPerMinute) {
    super(maxRequestsPerMinute);
  }
}
