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

  private void applyCommonHeaders(HttpSecurity http) throws Exception {
    http.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(cspHeader)));
  }

  @Bean
  @Profile({"default", "test"})
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
    applyCommonHeaders(http);
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
    applyCommonHeaders(http);
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
}
