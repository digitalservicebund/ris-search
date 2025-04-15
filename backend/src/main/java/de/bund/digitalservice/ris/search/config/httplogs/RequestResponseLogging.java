package de.bund.digitalservice.ris.search.config.httplogs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

/** Configuration class for setting up request and response logging. */
@Configuration
public class RequestResponseLogging {

  /**
   * Bean definition for configuring request and response logging.
   *
   * @return a configured Logbook instance for logging request and response information
   */
  @Bean
  public Logbook logRequestResponse() {
    var sink =
        new LogRequestAndResponseTogetherSink(
            new JsonHttpLogFormatter(), new DefaultHttpLogWriter());

    return Logbook.builder().strategy(new BodyOnlyIfStatusAtLeastStrategy(400)).sink(sink).build();
  }
}
