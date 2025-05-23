package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.config.security.ApiKeyRequestMatcher;
import de.bund.digitalservice.ris.search.config.security.AuthProperties;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Class to configure security */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  String jwkSetUri;

  @Value("${server.front-end-url}")
  String frontEndUrl;

  @Value("${server.docs-url}")
  String docsUrl;

  final AuthProperties authProperties;

  final String[] internalPaths = new String[] {"/actuator/**", "/internal/**"};

  public SecurityConfig(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  @Bean
  // For local development profile (default) allow usage of API without credentials
  @Profile({"default"})
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
    http.csrf(customizer -> customizer.ignoringRequestMatchers(internalPaths));
    return http.build();
  }

  /**
   * Method to configure security for the application
   *
   * @param http The http security object
   * @return The security filter chain
   * @throws Exception If an error occurs
   */
  @Bean
  @Profile({"staging"})
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            requests ->
                requests.requestMatchers(internalPaths).permitAll().anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    http.csrf(customizer -> customizer.ignoringRequestMatchers(internalPaths));
    return http.build();
  }

  @Bean
  @Profile({"prototype"})
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
  @Profile({"production", "test"})
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
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }

  /**
   * This converter extracts the list of user groups from a "groups" claim and builds a list of
   * GrantedAuthority using the "GROUP_" prefix.
   *
   * @return The JwtAuthenticationConverter
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("GROUP_");
    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("groups");

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
  }

  /**
   * By default, Keycloak assigns user roles to a "roles" object within the "realm_access" claim.
   * This converter extracts the list of user groups from "realm.groups" and builds a list of
   * GrantedAuthority using the "GROUP_" prefix.
   *
   * @return The JwtAuthenticationConverter
   */
  @SuppressWarnings("unused")
  public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
        jwt -> {
          Collection<String> groups = jwt.getClaim("groups");
          return groups.stream()
              .map(group -> new SimpleGrantedAuthority("GROUP_" + group))
              .collect(Collectors.toList());
        };

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
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
}
