package de.bund.digitalservice.ris.search.data;

import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class BaseDataTest {

  private static final Duration TOKEN_REFRESH_MARGIN = Duration.ofMinutes(1);

  private final OAuth2AuthorizedClientManager authorizedClientManager;

  private OAuth2AccessToken accessToken;

  public BaseDataTest(OAuth2AuthorizedClientManager authorizedClientManager) {
    this.authorizedClientManager = authorizedClientManager;
    this.accessToken = fetchAccessToken();
  }

  private OAuth2AccessToken fetchAccessToken() {
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
            .principal("client_credentials")
            .build();

    OAuth2AuthorizedClient authorizedClient =
        this.authorizedClientManager.authorize(authorizeRequest);
    return authorizedClient.getAccessToken();
  }

  public String getAccessToken() {
    if (this.isTokenExpiringSoon()) {
      this.accessToken = fetchAccessToken();
    }
    return this.accessToken.getTokenValue();
  }

  private boolean isTokenExpiringSoon() {
    Instant expiresAt = this.accessToken.getExpiresAt();
    return expiresAt == null || expiresAt.isBefore(Instant.now().plus(TOKEN_REFRESH_MARGIN));
  }

  public static double calculatePercentage(int part, int total) {
    if (total == 0) {
      return 0.0;
    }
    return ((double) part / total) * 100;
  }
}
