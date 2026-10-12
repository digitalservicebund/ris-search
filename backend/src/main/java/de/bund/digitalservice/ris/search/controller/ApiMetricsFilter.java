package de.bund.digitalservice.ris.search.controller;

import com.posthog.server.PostHogCaptureOptions;
import com.posthog.server.PostHogInterface;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** OncePerRequestFilter to monitor every api call excluding actuator calls */
@Component
public class ApiMetricsFilter extends OncePerRequestFilter {

  private final PostHogInterface postHogInterface;

  private final String excludedPathPrefix;

  /**
   * @param postHogInterface {@link PostHogInterface} used to capture api events
   * @param actuatorPath excluded actuator path
   */
  public ApiMetricsFilter(
      PostHogInterface postHogInterface,
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorPath) {
    this.postHogInterface = postHogInterface;
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
      // PostHog client is absent when not configured (e.g. local dev)
      Map<String, Object> properties = new HashMap<>();
      properties.put("path", request.getRequestURI());
      properties.put("method", request.getMethod());
      properties.put("status_code", response.getStatus());
      properties.put("user_agent", request.getHeader("User-Agent"));

      PostHogCaptureOptions opts = PostHogCaptureOptions.builder().properties(properties).build();
      // Capture the event — queued in memory and sent aggregated in background
      postHogInterface.capture(null, "api_call_performed", opts);
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getRequestURI().startsWith(excludedPathPrefix);
  }
}
