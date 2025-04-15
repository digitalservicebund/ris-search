package de.bund.digitalservice.ris.search.legacyportal.dto.api.norms;

import java.util.List;
import lombok.Builder;

@Builder
public record NormsApiListDTO(List<NormsApiDTO> data) {}
