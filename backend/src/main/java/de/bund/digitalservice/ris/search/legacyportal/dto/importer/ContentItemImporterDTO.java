package de.bund.digitalservice.ris.search.legacyportal.dto.importer;

import lombok.Builder;

@Builder
public record ContentItemImporterDTO(String href, String markerNumber, String description) {}
