package de.bund.digitalservice.ris.search.legacyportal.service;

import de.bund.digitalservice.ris.search.legacyportal.controller.helper.BuildLegislationDocumentURI;
import de.bund.digitalservice.ris.search.legacyportal.dto.api.norms.NormsApiListDTO;
import de.bund.digitalservice.ris.search.legacyportal.enums.LegalDocumentVersion;
import de.bund.digitalservice.ris.search.legacyportal.model.LegalDocument;
import de.bund.digitalservice.ris.search.legacyportal.repository.LegalDocumentRepository;
import de.bund.digitalservice.ris.search.legacyportal.transformer.api.NormsApiDTOTransformer;
import de.bund.digitalservice.ris.search.legacyportal.utils.LocalLegalDocumentUtils;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LegacyNormsService {

  private final LegalDocumentRepository legalDocumentRepository;

  @Autowired
  public LegacyNormsService(LegalDocumentRepository legalDocumentRepository) {
    this.legalDocumentRepository = legalDocumentRepository;
  }

  public Optional<NormsApiListDTO> getAllNormsBySearchQuery(
      String searchTerm, LegalDocumentVersion legalDocumentVersion) {
    var legalDocuments = getLegalDocumentsBySearchTermAndVersion(searchTerm, legalDocumentVersion);

    if (legalDocuments.isEmpty()) {
      return Optional.empty();
    }

    var normsApiDTOList =
        legalDocuments.get().stream().map(NormsApiDTOTransformer::getNormsApiDTO).toList();

    return Optional.of(NormsApiListDTO.builder().data(normsApiDTOList).build());
  }

  private Optional<List<LegalDocument>> getLegalDocumentsBySearchTermAndVersion(
      String searchTerm, LegalDocumentVersion legalDocumentVersion) {

    if (StringUtils.isNotEmpty(searchTerm)) {
      return legalDocumentRepository.findAllLegislationByTitleAndVersionCustomQuery(
          legalDocumentVersion.getVersion(), searchTerm);
    }

    return legalDocumentRepository.findAllLegislationByVersionCustomQuery(
        legalDocumentVersion.getVersion());
  }

  public Optional<byte[]> getNormDocumentByAnnouncementParameters(
      BuildLegislationDocumentURI buildLegislationDocumentURI) {
    String documentUriParameter = buildLegislationDocumentURI.getDocumentURI();

    LegalDocument result = legalDocumentRepository.getByDocumentUri(documentUriParameter);

    if (result == null) {
      return Optional.empty();
    }

    if (result.getVersion().equalsIgnoreCase(LegalDocumentVersion.VERSION_1_4.getVersion())) {
      return LocalLegalDocumentUtils.getFile(
          LocalLegalDocumentUtils.LDML_1_4_FOLDER, result.getXmlFilePath());
    } else {
      return LocalLegalDocumentUtils.getFile(
          LocalLegalDocumentUtils.LDML_1_6_FOLDER, result.getXmlFilePath());
    }
  }
}
