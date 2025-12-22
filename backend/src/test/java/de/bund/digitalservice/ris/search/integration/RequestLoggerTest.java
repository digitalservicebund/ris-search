package de.bund.digitalservice.ris.search.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.httplogs.RequestLogger;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class RequestLoggerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;

  private static Logger logger;
  private static Level originalLevel;

  @BeforeAll
  static void setup() {
    // set log level to trace to test log output
    logger = (Logger) LoggerFactory.getLogger(RequestLogger.class);
    originalLevel = logger.getLevel();
    logger.setLevel(Level.TRACE);
  }

  @AfterAll
  static void restore() {
    // restore original configured log level
    logger.setLevel(originalLevel);
  }

  @Test
  void itLogsUriQueryParamsAndStatusCode(CapturedOutput capturedOutput) throws Exception {
    String query = "?searchTerm=Test";

    mockMvc
        .perform(get(ApiConfig.Paths.DOCUMENT + query).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk());

    assertThat(capturedOutput.getOut())
        .contains("{\"path\":\"/v1/document\",\"queryParams\":\"searchTerm=Test\",\"status\":200}");
  }
}
