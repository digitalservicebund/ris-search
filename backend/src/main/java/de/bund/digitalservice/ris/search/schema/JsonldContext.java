package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;

/** Interface to set url of remote jsonld context */
public interface JsonldContext {

  @JsonProperty("@context")
  default String getContext() {
    return ApiConfig.Paths.JSONLD_CONTEXT;
  }
}
