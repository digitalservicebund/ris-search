package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.security.ApiKeyRequestMatcher;
import de.bund.digitalservice.ris.search.config.security.AuthProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("${server.front-end-url}")
  String frontEndUrl;

  @Value("${server.docs-url}")
  String docsUrl;

  final AuthProperties authProperties;

  final String[] internalPaths = new String[] {"/actuator/**", "/internal/**", "/nlex"};

  public SecurityConfig(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  @Bean
  // For local development profile (default) allow usage of API without credentials
  @Profile({"default", "test"})
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
    http.csrf(customizer -> customizer.ignoringRequestMatchers(internalPaths));
    return http.build();
  }

  @Bean
  @Profile({"prototype", "staging", "uat"})
  public SecurityFilterChain prototypeSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        requests ->
            requests
                .requestMatchers(internalPaths)
                .permitAll()
                .requestMatchers("/v1/**")
                .permitAll()
                .anyRequest()
                .authenticated());
    http.csrf(customizer -> customizer.ignoringRequestMatchers(internalPaths));
    return http.build();
  }

  /**
   * Method to configure security for the application. Uses a custom {@link
   * AuthenticationManagerResolver} to allow for usage of bearer tokens for certain requests.
   *
   * @param http The http security object
   * @return The security filter chain
   * @throws Exception If an error occurs
   */
  @Bean
  @Profile({"production"})
  public SecurityFilterChain productionSecurityFilterChain(
      HttpSecurity http, ApiKeyRequestMatcher apiKeyRequestMatcher) throws Exception {
    http.authorizeHttpRequests(
        requests ->
            requests
                .requestMatchers(
                    "/actuator/**", "/.well-known/**", "/swagger-ui/**", "/v3/**", "/api/**")
                .permitAll()
                .requestMatchers(apiKeyRequestMatcher)
                .permitAll()
                .anyRequest()
                .authenticated());
    return http.build();
  }

  /**
   * Method to configure CORS
   *
   * @return The web mvc configurer
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {

    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedMethods(CorsConfiguration.ALL)
            .allowedHeaders(CorsConfiguration.ALL)
            .allowedOrigins(frontEndUrl, docsUrl);
      }
    };
  }

  /**
   * Method to configure CORS for E2E tests.
   *
   * @return The web mvc configurer
   */
  @Bean
  @Profile("e2e")
  public WebMvcConfigurer e2eCorsConfigurer() {

    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedMethods(CorsConfiguration.ALL)
            .allowedHeaders(CorsConfiguration.ALL)
            .allowedOrigins(CorsConfiguration.ALL);
      }
    };
  }
}
