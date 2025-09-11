package de.bund.digitalservice.ris.search.unit.importer.caselaw;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaseLawImporterTest {
  private String testCaseLawLdml;
  private CaseLawRepository caseLawRepositoryMock;
  private CaseLawBucket caseLawBucket;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  @BeforeEach
  void beforeEach() throws IOException {
    testCaseLawLdml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    caseLawRepositoryMock = Mockito.mock(CaseLawRepository.class);
    caseLawBucket = Mockito.mock(CaseLawBucket.class);
  }

  @Test
  @DisplayName("Import one caselaw ldml")
  void canImportLdml() throws ObjectStoreServiceException {
    IndexCaselawService indexCaselawService =
        new IndexCaselawService(caseLawBucket, caseLawRepositoryMock);
    Changelog mockChangelog = new Changelog();
    mockChangelog.setChangeAll(true);
    when(caseLawBucket.getAllKeys()).thenReturn(List.of("mockFile.xml"));
    when(caseLawBucket.getFileAsString("mockFile.xml")).thenReturn(Optional.of(testCaseLawLdml));
    indexCaselawService.indexChangelog(mockChangelog);
    verify(caseLawRepositoryMock, atLeastOnce()).save(any());
  }
}
