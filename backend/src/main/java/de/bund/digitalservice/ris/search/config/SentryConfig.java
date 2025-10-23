package de.bund.digitalservice.ris.search.config;

import io.sentry.SentryOptions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Class to configure Sentry */
@Configuration
public class SentryConfig {

  @Bean
  SentryOptions.TracesSamplerCallback tracesSamplerCallback() {
    return context -> {
      HttpServletRequest request =
          (HttpServletRequest) context.getCustomSamplingContext().get("request");
      String url = request.getRequestURI();
      if (url.startsWith("/actuator")) {
        return 0d;
      } else {
        return null;
      }
    };
  }
}
