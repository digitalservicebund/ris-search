package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexCaseLawServiceTest {

  IndexCaselawService service;

  @Mock CaseLawBucket bucket;
  @Mock CaseLawRepository repo;
  @Mock CaseLawLdmlToOpenSearchMapper marshaller;

  @BeforeEach()
  void setup() {
    this.service = new IndexCaselawService(bucket, repo, marshaller);
  }

  CaseLawDocumentationUnit testUnit = CaseLawDocumentationUnit.builder().id("TEST80020093").build();

  @Test
  void reindexAllIgnoresInvalidFiles() throws ObjectStoreServiceException {
    when(this.bucket.getAllKeys()).thenReturn(List.of("file1.xml", "file2.xml"));
    when(this.bucket.getFileAsString("file1.xml")).thenReturn(Optional.of("content"));
    when(this.marshaller.fromString("content")).thenReturn(testUnit);
    when(this.bucket.getFileAsString("file2.xml")).thenReturn(Optional.of("this will not parse"));
    when(this.marshaller.fromString("this will not parse"))
        .thenThrow(OpenSearchMapperException.class);

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST80020093");
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itCanReindexFromOneSpecificChangelog() throws ObjectStoreServiceException {
    when(this.bucket.getFileAsString("TEST080020093.xml")).thenReturn(Optional.of("content"));
    when(marshaller.fromString("content")).thenReturn(testUnit);

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("TEST080020093.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST80020093");
                  return true;
                }));
  }

  @Test
  void doesNotIndexNoneXmlFilesListedInChangelog() throws ObjectStoreServiceException {
    when(this.bucket.getFileAsString("TEST080020093/TEST080020093.xml"))
        .thenReturn(Optional.of("content"));

    when(marshaller.fromString("content")).thenReturn(testUnit);

    Changelog changelog = new Changelog();
    changelog.setChanged(
        Sets.newHashSet(List.of("TEST080020093/TEST080020093.xml", "TEST080020093/picture.png")));
    service.indexChangelog(changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST80020093");
                  return true;
                }));
  }

  @Test
  void itCanDeleteFromOneSpecificChangelog() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setDeleted(Sets.newHashSet(Set.of("TEST080020093.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1)).deleteAllById(Set.of("TEST080020093"));
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllKeys())
        .thenReturn(
            List.of(
                "TEST080020093/TEST080020093.xml",
                "TEST080020093/TEST080020094.xml",
                "changelogs/2025-03-26T14:13:34.096304815Z-caselaw.json"));
    assertThat(service.getNumberOfIndexableDocumentsInBucket()).isEqualTo(2);
  }
}
