package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.ratelimiting.RateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.UrlHandlerFilter;

/**
 * Web MVC configuration that registers application-specific filters.
 *
 * <p>Registers a rate limiting filter for the feedback endpoint and a default rate limiter for all
 * requests.
 */
@Configuration
public class WebMvcConfig {

  /**
   * Applies a {@link RateLimitFilter} to the feedback endpoint.
   *
   * <p>Both rate limit registrations use the same filter class, so each needs a distinct name.
   * Otherwise the servlet container would only register one of them.
   *
   * @param maxRequests maximum number of requests per client IP within the time window
   * @param seconds duration of the time window in seconds
   * @return the registration of the feedback rate limit filter
   */
  @Bean
  public FilterRegistrationBean<RateLimitFilter> feedbackRateLimitFilterRegistration(
      @Value("${rate-limit.feedback.requests}") int maxRequests,
      @Value("${rate-limit.feedback.seconds}") int seconds) {
    FilterRegistrationBean<RateLimitFilter> registration =
        new FilterRegistrationBean<>(new RateLimitFilter(maxRequests, seconds));
    registration.setName("feedbackRateLimitFilter");
    registration.addUrlPatterns(ApiConfig.Paths.FEEDBACK);
    return registration;
  }

  /**
   * Applies a {@link RateLimitFilter} to all requests.
   *
   * @param maxRequests maximum number of requests per client IP within the time window
   * @param seconds duration of the time window in seconds
   * @return the registration of the default rate limit filter
   */
  @Bean
  public FilterRegistrationBean<RateLimitFilter> defaultRateLimitFilterRegistration(
      @Value("${rate-limit.default.requests}") int maxRequests,
      @Value("${rate-limit.default.seconds}") int seconds) {
    FilterRegistrationBean<RateLimitFilter> registration =
        new FilterRegistrationBean<>(new RateLimitFilter(maxRequests, seconds));
    registration.setName("defaultRateLimitFilter");
    return registration;
  }

  /**
   * Configures a {@link UrlHandlerFilter} to globally manage trailing slashes in URLs. This filter
   * intercepts requests and performs a client-side redirect to the same path without a trailing
   * slash.
   *
   * @return a configured {@link UrlHandlerFilter}
   */
  @Bean
  public UrlHandlerFilter urlHandlerFilter() {
    return UrlHandlerFilter.trailingSlashHandler("/**")
        .redirect(HttpStatus.PERMANENT_REDIRECT)
        .build();
  }
}
