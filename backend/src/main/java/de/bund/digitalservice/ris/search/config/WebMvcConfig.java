package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.ratelimiting.DefaultRateLimitFilter;
import de.bund.digitalservice.ris.search.config.ratelimiting.FeedbackRateLimitFilter;
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
   * Restricts the {@link FeedbackRateLimitFilter} to the feedback endpoint. Without an explicit
   * registration, Spring Boot would register the filter bean for all requests.
   *
   * @param filter filter handling feedback-related rate limits
   * @return the registration of the feedback rate limit filter
   */
  @Bean
  public FilterRegistrationBean<FeedbackRateLimitFilter> feedbackRateLimitFilterRegistration(
      FeedbackRateLimitFilter filter) {
    FilterRegistrationBean<FeedbackRateLimitFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.addUrlPatterns(ApiConfig.Paths.FEEDBACK);
    return registration;
  }

  /**
   * Registers the {@link DefaultRateLimitFilter} for all requests.
   *
   * @param filter filter applying default rate limits to requests
   * @return the registration of the default rate limit filter
   */
  @Bean
  public FilterRegistrationBean<DefaultRateLimitFilter> defaultRateLimitFilterRegistration(
      DefaultRateLimitFilter filter) {
    return new FilterRegistrationBean<>(filter);
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
