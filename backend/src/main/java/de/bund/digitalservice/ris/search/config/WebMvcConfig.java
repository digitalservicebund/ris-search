package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.ratelimiting.FeedbackRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final FeedbackRateLimitInterceptor feedbackInterceptor;

  WebMvcConfig(FeedbackRateLimitInterceptor interceptor) {
    this.feedbackInterceptor = interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(feedbackInterceptor).addPathPatterns(ApiConfig.Paths.FEEDBACK);
    // registry.addInterceptor("/v1")
  }
}
