package de.bund.digitalservice.ris.search.util;

import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class OAuthTokenProvider {

  private static final Duration TOKEN_REFRESH_MARGIN = Duration.ofMinutes(1);
  private static final String CLIENT_REGISTRATION_ID = "keycloak";
  private static final String PRINCIPAL = "client_credentials";
  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private OAuth2AccessToken accessToken;

  public OAuthTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
    this.authorizedClientManager = authorizedClientManager;
  }

  public String getTokenValue() {
    if (accessToken == null || isTokenExpiringSoon()) {
      accessToken = fetchAccessToken();
    }
    return accessToken.getTokenValue();
  }

  private OAuth2AccessToken fetchAccessToken() {
    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
            .principal(PRINCIPAL)
            .build();

    OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
    return authorizedClient.getAccessToken();
  }

  private boolean isTokenExpiringSoon() {
    Instant expiresAt = accessToken.getExpiresAt();
    return expiresAt == null || expiresAt.isBefore(Instant.now().plus(TOKEN_REFRESH_MARGIN));
  }
}
