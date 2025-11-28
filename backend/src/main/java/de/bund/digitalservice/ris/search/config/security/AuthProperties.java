package de.bund.digitalservice.ris.search.config.security;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties holding API key definitions used for authenticating requests.
 *
 * <p>Bound from application properties under the "server" prefix. Each ApiKey entry contains the
 * hashed key value and an optional prefix used to distinguish keys (not part of the hash).
 */
@Data
@Component
@ConfigurationProperties(prefix = "server")
public class AuthProperties {
  private List<ApiKey> apiKeys;

  /**
   * Represents an API key used for authenticating requests.
   *
   * <p>Each API key consists of a hashed key value and an optional prefix. The prefix is used to
   * differentiate between keys but is not included in the hash comparison process. This class is
   * typically a part of the configuration properties mapped to the application configuration using
   * the "server" prefix.
   */
  @Data
  public static class ApiKey {
    private String hash;

    /**
     * A prefix, e.g. v1_, that may be used to distinguish different keys. It will not be compared
     * to the hash.
     */
    private String prefix;
  }
}
