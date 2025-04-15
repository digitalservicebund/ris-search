package de.bund.digitalservice.ris.search.legacyportal.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class ContentItem {

  private String href;
  private String markerNumber;
  private String description;
}
