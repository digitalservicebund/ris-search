package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** OncePerRequestFilter to monitor every api call below {@link ApiConfig.Paths#BASE} */
@Component
@ConditionalOnProperty(name = "posthog.track-api-calls", havingValue = "true")
public class ApiMetricsFilter extends OncePerRequestFilter {

  /** Header set by the portal frontend to mark its own requests. Not authenticated. */
  public static final String PORTAL_HEADER = "X-RIS-PORTAL";

  private final PostHogClient postHogClient;

  /**
   * @param postHogClient {@link PostHogClient} used to capture api events
   */
  public ApiMetricsFilter(PostHogClient postHogClient) {
    this.postHogClient = postHogClient;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    postHogClient.submitApiEvent(
        request.getServletPath(), request.getHeader("User-Agent"), isPortalRequest(request));
    filterChain.doFilter(request, response);
  }

  private static boolean isPortalRequest(HttpServletRequest request) {
    return "true".equalsIgnoreCase(request.getHeader(PORTAL_HEADER));
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // skip CORS preflights
    return HttpMethod.OPTIONS.matches(request.getMethod())
        || !request.getServletPath().startsWith(ApiConfig.Paths.BASE);
  }
}
