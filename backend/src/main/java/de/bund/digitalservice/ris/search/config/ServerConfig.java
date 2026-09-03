package de.bund.digitalservice.ris.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Class to hold configurations from the server prefix */
@ConfigurationProperties(prefix = "server")
@Configuration
@Data
public class ServerConfig {
  private String backEndUrl;
}
