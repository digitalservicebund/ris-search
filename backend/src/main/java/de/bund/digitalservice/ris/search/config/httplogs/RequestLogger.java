package de.bund.digitalservice.ris.search.config.httplogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/** GenericFilterBean to log all incoming http Requests */
@Component
public class RequestLogger extends GenericFilterBean {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    chain.doFilter(request, response);

    log((HttpServletRequest) request, (HttpServletResponse) response);
  }

  private void log(HttpServletRequest request, HttpServletResponse response) throws IOException {
    RequestLog log =
        new RequestLog(request.getRequestURI(), request.getQueryString(), response.getStatus());
    String msg = mapper.writeValueAsString(log);
    logger.trace(msg);
  }
}
