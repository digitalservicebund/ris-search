package de.bund.digitalservice.ris.search.legacyportal.transformer.api;

import de.bund.digitalservice.ris.search.legacyportal.dto.api.norms.NormsApiDTO;
import de.bund.digitalservice.ris.search.legacyportal.model.LegalDocument;

public class NormsApiDTOTransformer {

  private NormsApiDTOTransformer() {}

  public static NormsApiDTO getNormsApiDTO(LegalDocument legalDocument) {
    return NormsApiDTO.builder()
        .guid(legalDocument.getGlobalUID())
        .officialLongTitle(legalDocument.getDocTitle())
        .eli(legalDocument.getDocumentUri())
        .build();
  }
}
