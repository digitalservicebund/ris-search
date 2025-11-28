package de.bund.digitalservice.ris.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to define Cross-Origin Resource Sharing (CORS) policies for the application.
 *
 * <p>This class provides configurations for handling HTTP requests from different origins, enabling
 * or restricting access to the application resources based on the provided settings. The class
 * supports separate configurations for private (staging, UAT, production) and public (e2e,
 * prototype, default) environments.
 *
 * <p>The configuration allows defining permitted HTTP methods, headers, and origins for the URLs
 * exposed by the application.
 *
 * <p>The CORS policies are configured via Spring's WebMvcConfigurer within the methods provided.
 */
@Configuration
public class CorsConfig {

  private final String frontEndUrl;
  private final String docsUrl;

  /**
   * Constructor for the CorsConfig class, which initializes the configuration for Cross-Origin
   * Resource Sharing (CORS) handling.
   *
   * @param frontEndUrl The URL of the front-end application, used in CORS configuration.
   * @param docsUrl The URL of the documentation, used in CORS configuration.
   */
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
