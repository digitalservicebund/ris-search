package de.bund.digitalservice.ris.search.legacyportal.config.opensearch;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LegacyConfigurations {

  @Value("${opensearch.host}")
  private String host;

  @Value("${opensearch.port}")
  private String port;
}
