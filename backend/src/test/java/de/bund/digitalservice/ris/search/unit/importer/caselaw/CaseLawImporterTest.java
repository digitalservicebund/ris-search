package de.bund.digitalservice.ris.search.unit.importer.caselaw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.utils.CaseLawLdmlTemplateUtils;
import java.io.IOException;
import java.time.LocalDate;
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
  private CaseLawSynthesizedRepository caseLawSynthesizedRepositoryMock;
  private CaseLawBucket caseLawBucket;
  private final CaseLawLdmlTemplateUtils caseLawLdmlTemplateUtils = new CaseLawLdmlTemplateUtils();

  @BeforeEach
  void beforeEach() throws IOException {
    testCaseLawLdml = caseLawLdmlTemplateUtils.getXmlFromTemplate(null);
    caseLawSynthesizedRepositoryMock = Mockito.mock(CaseLawSynthesizedRepository.class);
    caseLawBucket = Mockito.mock(CaseLawBucket.class);
  }

  @Test
  @DisplayName("Import one caselaw ldml")
  void canImportLdml() throws ObjectStoreException {
    IndexCaselawService indexCaselawService =
        new IndexCaselawService(caseLawBucket, caseLawSynthesizedRepositoryMock);
    Changelog mockChangelog = new Changelog();
    mockChangelog.setChangeAll(true);
    when(caseLawBucket.getAllFilenames()).thenReturn(List.of("mockFile.xml"));
    when(caseLawBucket.getFileAsString("mockFile.xml")).thenReturn(Optional.of(testCaseLawLdml));
    indexCaselawService.indexChangelog("mockChangelogFileName", mockChangelog);
    verify(caseLawSynthesizedRepositoryMock, atLeastOnce()).save(any());
  }

  @Test
  // Also tests striping xml tags
  void importerShouldMapToCaseLawDocumentationUnitCorrectly() {
    IndexCaselawService indexCaselawService =
        new IndexCaselawService(caseLawBucket, caseLawSynthesizedRepositoryMock);
    Optional<CaseLawDocumentationUnit> parseResult =
        indexCaselawService.parseOneDocument("mockFileName", testCaseLawLdml);
    assertTrue(parseResult.isPresent());
    CaseLawDocumentationUnit caseLaw = parseResult.get();

    assertEquals("testDocNumber", caseLaw.id());
    assertEquals("documentationOffice", caseLaw.documentationOffice());
    assertEquals(
        "Example Tatbestand/CaseFacts. More background even more background", caseLaw.caseFacts());
    assertEquals("Example Entscheidungsgründe/DecisionGrounds", caseLaw.decisionGrounds());
    assertEquals("testDocNumber", caseLaw.documentNumber());
    assertEquals("testEcli", caseLaw.ecli());
    assertEquals("Example Leitsatz/GuidingPrinciple", caseLaw.guidingPrinciple());
    assertEquals("Title", caseLaw.headline());
    assertEquals(LocalDate.of(2020, 1, 1), caseLaw.decisionDate());
    assertEquals("Example Tenor/Tenor", caseLaw.tenor());
    assertEquals("PUBLISHED", caseLaw.publicationStatus());
    assertEquals("[Test file number 1, Test file number 2]", caseLaw.fileNumbers().toString());

    assertEquals("Test court type", caseLaw.courtType());
    assertEquals("Test court location", caseLaw.location());
    assertEquals("Test court type Test court location", caseLaw.courtKeyword());
    assertEquals("Test document type", caseLaw.documentType());
    assertEquals("Example Gliederung/Outline", caseLaw.outline());
    assertEquals("Test judicial body", caseLaw.judicialBody());
    assertEquals("[keyword1, keyword2]", caseLaw.keywords().toString());
    assertEquals("[Test decision name]", caseLaw.decisionName().toString());
    assertEquals("[Test deviatingDocumentNumber]", caseLaw.deviatingDocumentNumber().toString());
  }
}
