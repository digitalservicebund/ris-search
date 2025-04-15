package de.bund.digitalservice.ris.search.legacyportal.controller.helper;

import de.bund.digitalservice.ris.search.legacyportal.config.ApiConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BuildLegislationDocumentURI {

  private static final String SLASH = "/";

  private String printAnnouncementGazette;
  private int printAnnouncementYear;
  private String printAnnouncementPage;

  public String getDocumentURI() {
    return ApiConfig.API_EUROPEAN_LEGISLATION_IDENTIFIER
        + SLASH
        + this.printAnnouncementGazette
        + SLASH
        + this.printAnnouncementYear
        + SLASH
        + this.printAnnouncementPage;
  }
}
