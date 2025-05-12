package de.bund.digitalservice.ris.search.controller.api.utils;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public enum ResourceResolutionMode {
  BASE,
  PROXY;

  /**
   * If legislation is accessed through the proxy, an `/api` prefix will be required. To prevent URL
   * injection, only a limited set of values should be allowed.
   *
   * @return The base URL to use for resources like images.
   */
  @NotNull
  public String getResourcesBasePath() {
    Map<ResourceResolutionMode, String> allowedValues =
        Map.of(BASE, ApiConfig.Paths.LEGISLATION, PROXY, "/api" + ApiConfig.Paths.LEGISLATION);

    String resolvedValue = allowedValues.get(this);
    return resolvedValue + "/";
  }
}
