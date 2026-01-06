package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Interface defining a JsonLd Type in Schema files */
public interface JsonldResource {
  @JsonProperty(value = "@type", index = 0)
  public String getType();
}
