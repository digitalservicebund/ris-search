package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.security.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final RateLimitInterceptor interceptor;

  WebMvcConfig(RateLimitInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor).addPathPatterns(ApiConfig.Paths.FEEDBACK);
  }
}
