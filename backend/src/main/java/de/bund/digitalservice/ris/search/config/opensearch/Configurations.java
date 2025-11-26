package de.bund.digitalservice.ris.search.config.opensearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Class with base configurations for connecting with OpenSearch */
@Data
@Configuration
@ConfigurationProperties(prefix = "opensearch")
public class Configurations {

  /**
   * No-argument constructor used by Spring for properties binding.
   *
   * <p>Creates an empty Configurations instance which will be populated by the framework.
   */
  public Configurations() {
    // default constructor for @ConfigurationProperties binding
  }

  private String host;

  private String port;

  private String username;

  private String password;

  private String normsIndexName;

  private String caseLawsIndexName;

  private String literatureIndexName;

  private String administrativeDirectiveIndexName;

  private String documentsAliasName;
}
