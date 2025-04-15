package de.bund.digitalservice.ris.search.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Filter implementation to handle trailing slashes in URLs. */
@Component
public class TrailingSlashFilter implements Filter {

  private static final String QUESTION_MARK_SYMBOL = "?";

  /**
   * Performs filtering to remove trailing slashes from URLs.
   *
   * @param request the servlet request
   * @param response the servlet response
   * @param chain the filter chain
   * @throws IOException if an I/O error occurs
   * @throws ServletException if a servlet error occurs
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    var httpServletRequest = (HttpServletRequest) request;
    String uri = httpServletRequest.getRequestURI();
    if (uri.endsWith("/") && uri.length() > 1) {
      String targetUrl = uri.substring(0, uri.length() - 1);

      String queryString =
          StringUtils.isNotEmpty(httpServletRequest.getQueryString())
              ? QUESTION_MARK_SYMBOL + httpServletRequest.getQueryString()
              : "";

      ((HttpServletResponse) response).sendRedirect(targetUrl + queryString);
    } else {
      chain.doFilter(request, response);
    }
  }
}
