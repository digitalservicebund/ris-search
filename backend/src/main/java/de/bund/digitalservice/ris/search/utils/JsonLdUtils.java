package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** A utility class for working with JSON-LD (JSON for Linked Data). */
@Component
public class JsonLdUtils {

  private String backEndUrl;

  @Value("${server.back-end-url}")
  public void setBackEndUrl(String url) {
    backEndUrl = Strings.CS.removeEnd(url, "/");
  }

  public String getJsonldPath() {
    return backEndUrl + ApiConfig.Paths.JSONLD_CONTEXT;
  }
}
