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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** OncePerRequestFilter to monitor every api call below {@link ApiConfig.Paths#BASE} */
@Component
@ConditionalOnProperty(name = "posthog.track-api-calls", havingValue = "true")
public class ApiMetricsFilter extends OncePerRequestFilter {

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

    postHogClient.submitApiEvent(request.getServletPath(), request.getHeader("User-Agent"));
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return !path.startsWith(ApiConfig.Paths.BASE);
  }
}
