package de.bund.digitalservice.ris.search.config.ratelimiting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRateLimitInterceptor extends RateLimitInterceptor {

  public FeedbackRateLimitInterceptor(
      @Value("${rate-limit.feedback.requests}") int maxRequests,
      @Value("${rate-limit.feedback.seconds}") int seconds) {
    super(maxRequests, seconds);
  }
}
