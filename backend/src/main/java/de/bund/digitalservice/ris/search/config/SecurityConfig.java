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

/**
 * Configures HTTP security for the application across different profiles.
 *
 * <p>Provides SecurityFilterChain beans for default/test, prototype/staging/uat and production
 * environments. Common security headers and request authorization rules are applied here.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  final AuthProperties authProperties;

  final String[] internalPaths = new String[] {"/actuator/**", "/nlex"};

  @Value("${app.security.csp-header}")
  private String cspHeader;

  public SecurityConfig(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  private void applyCommonConfiguration(HttpSecurity http) {
    http.csrf(customizer -> customizer.ignoringRequestMatchers(internalPaths));
    http.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(cspHeader)));
  }

  /**
   * Configures and builds a security filter chain for the "default" and "test" profiles. This
   * filter chain allows unrestricted access to all requests and applies common security headers.
   *
   * @param http the {@link HttpSecurity} object used to define security configurations
   * @return a {@link SecurityFilterChain} instance representing the configured HTTP security filter
   */
  @Bean
  @Profile({"default", "test"})
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
    applyCommonConfiguration(http);
    return http.build();
  }

  /**
   * Configures and builds a security filter chain for "prototype", "staging", and "uat" profiles.
   * This filter chain applies authentication rules that permit access to certain internal paths and
   * versioned API endpoints, while requiring authentication for all other requests. Additionally,
   * common security headers are applied.
   *
   * @param http the {@link HttpSecurity} object used to define security configurations
   * @return a {@link SecurityFilterChain} instance representing the configured HTTP security filter
   */
  @Bean
  @Profile({"prototype", "staging", "uat"})
  public SecurityFilterChain prototypeSecurityFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(
        requests ->
            requests
                .requestMatchers(internalPaths)
                .permitAll()
                .requestMatchers("/v1/**")
                .permitAll()
                .anyRequest()
                .authenticated());
    applyCommonConfiguration(http);
    return http.build();
  }

  /**
   * Method to configure security for the application. Uses a custom {@link
   * AuthenticationManagerResolver} to allow for usage of bearer tokens for certain requests.
   *
   * @param http The http security object
   * @param apiKeyRequestMatcher The request matcher for API key authentication
   * @return The security filter chain
   */
  @Bean
  @Profile({"production"})
  public SecurityFilterChain productionSecurityFilterChain(
      HttpSecurity http, ApiKeyRequestMatcher apiKeyRequestMatcher) {
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
}
