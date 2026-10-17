package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** OncePerRequestFilter to monitor every api call excluding actuator calls */
@Component
public class ApiMetricsFilter extends OncePerRequestFilter {

  private final PostHogClient postHogClient;

  private final String excludedPathPrefix;

  /**
   * @param postHogClient {@link PostHogClient} used to capture api events
   * @param actuatorPath excluded actuator path
   */
  public ApiMetricsFilter(
      PostHogClient postHogClient,
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorPath) {
    this.postHogClient = postHogClient;
    this.excludedPathPrefix = actuatorPath;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      postHogClient.submitApiEvent(
          request.getRequestURI(), request.getMethod(), request.getHeader("User-Agent"));
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getRequestURI().startsWith(excludedPathPrefix);
  }
}
