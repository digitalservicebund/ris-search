package de.bund.digitalservice.ris.search.api.schema.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Interface defining a JsonLd Type in Schema files */
public interface JsonldResource {
  @JsonProperty(value = "@type", index = 0)
  public String getType();
}
