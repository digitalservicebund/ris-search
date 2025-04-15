package de.bund.digitalservice.ris.search.config.security;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server")
public class AuthProperties {
  private List<ApiKey> apiKeys;

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
