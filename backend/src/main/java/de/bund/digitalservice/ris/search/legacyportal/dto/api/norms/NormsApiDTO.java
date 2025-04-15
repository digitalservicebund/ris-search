package de.bund.digitalservice.ris.search.legacyportal.dto.api.norms;

import lombok.Builder;

@Builder
public record NormsApiDTO(String guid, String officialLongTitle, String eli) {}
