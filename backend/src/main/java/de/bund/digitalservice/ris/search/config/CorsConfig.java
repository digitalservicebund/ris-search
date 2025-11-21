package de.bund.digitalservice.ris.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  private final String frontEndUrl;
  private final String docsUrl;

  public CorsConfig(
      @Value("${server.front-end-url}") String frontEndUrl,
      @Value("${server.docs-url}") String docsUrl) {

    this.frontEndUrl = frontEndUrl;
    this.docsUrl = docsUrl;
  }

  /**
   * Method to configure CORS for private applications.
   *
   * @return The web mvc configurer
   */
  @Bean
  @Profile({"staging", "uat", "production"})
  public WebMvcConfigurer privateCorsConfigurer() {

    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/v1/**")
            .allowedMethods("GET", "HEAD", "OPTIONS")
            .allowedHeaders(CorsConfiguration.ALL)
            .allowedOrigins(frontEndUrl, docsUrl);
      }
    };
  }

  /**
   * Method to configure CORS for public application and E2E tests.
   *
   * @return The web mvc configurer
   */
  @Bean
  @Profile({"e2e", "prototype", "default"})
  public WebMvcConfigurer publicCorsConfigurer() {

    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {

        registry
            .addMapping("/v1/feedback")
            .allowedMethods("GET")
            .allowedOriginPatterns(frontEndUrl, docsUrl)
            .allowedHeaders(CorsConfiguration.ALL);

        registry
            .addMapping("/v1/**")
            .allowedMethods("GET", "HEAD", "OPTIONS")
            .allowedOrigins(CorsConfiguration.ALL)
            .allowedHeaders(CorsConfiguration.ALL);
      }
    };
  }
}
