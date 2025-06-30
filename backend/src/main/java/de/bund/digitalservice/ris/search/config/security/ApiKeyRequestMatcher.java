package de.bund.digitalservice.ris.search.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * A RequestMatcher that determines whether a request has a valid API key in the X-Api-Key header.
 * The API key may have a prefix to differentiate different keys. It is compared to a hash stored in
 * {@link AuthProperties}.
 *
 * <p>See also: `doc/readme/api-keys.md`.
 */
@Component
public class ApiKeyRequestMatcher implements RequestMatcher {

  private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder =
      Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

  private final AuthProperties authProperties;

  @Autowired
  public ApiKeyRequestMatcher(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    String auth = request.getHeader("X-Api-Key");
    if (auth == null) {
      return false;
    }
    var apiKey =
        authProperties.getApiKeys().stream()
            .filter(key -> auth.startsWith(key.getPrefix()))
            .findFirst();
    if (apiKey.isEmpty()) return false;

    int prefixLength = apiKey.get().getPrefix().length();
    String token = auth.substring(prefixLength);

    String expectedHash = apiKey.get().getHash();
    return pbkdf2PasswordEncoder.matches(token, expectedHash);
  }
}
