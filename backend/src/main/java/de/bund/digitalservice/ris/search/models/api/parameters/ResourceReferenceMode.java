package de.bund.digitalservice.ris.search.models.api.parameters;

/**
 * This enum indicates how resources that are referenced in HTML (e.g., images) should be prefixed.
 */
public enum ResourceReferenceMode {
  /** Use the same absolute path as the HTML page they are referenced in. */
  API,
  /** Use the Nuxt middleware proxy, for authentication. */
  PROXY;

  public static final String DEFAULT_VALUE = "API";
}
