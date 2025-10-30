package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.literature.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import de.bund.digitalservice.ris.search.service.IndexLiteratureService;
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
class IndexLiteratureServiceTest {

  IndexLiteratureService service;

  @Mock LiteratureBucket bucket;
  @Mock LiteratureRepository repo;

  @BeforeEach()
  void setup() {
    this.service = new IndexLiteratureService(bucket, repo);
  }

  @Test
  void reindexAllIgnoresInvalidFiles() throws ObjectStoreServiceException {
    var filenameA = "XXLU000000001.akn.xml";
    var filenameB = "XXLU000000002.akn.xml";
    final String xml = LoadXmlUtils.loadXmlAsString(Literature.class, filenameA);

    when(this.bucket.getAllKeys()).thenReturn(List.of(filenameA, filenameB));
    when(this.bucket.getFileAsString(filenameA)).thenReturn(Optional.of(xml));
    when(this.bucket.getFileAsString(filenameB)).thenReturn(Optional.of("this will not parse"));

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("XXLU000000001");
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void reindexAllIgnoresFilesNotIdentifiedAsDependentLiterature()
      throws ObjectStoreServiceException {
    var filenameA = "XXLU000000001.akn.xml"; // Only file identified as dependent literature
    var filenameB = "XXLS000000002.akn.xml";
    var filenameC = "XLU0000000002.akn.xml";
    var filenameD = "XXXLU000000002.akn.xml";
    final String xml = LoadXmlUtils.loadXmlAsString(Literature.class, filenameA);

    when(this.bucket.getAllKeys()).thenReturn(List.of(filenameA, filenameB, filenameC, filenameD));
    when(this.bucket.getFileAsString(filenameA)).thenReturn(Optional.of(xml));
    when(this.bucket.getFileAsString(filenameB)).thenReturn(Optional.of(xml));
    when(this.bucket.getFileAsString(filenameC)).thenReturn(Optional.of(xml));
    when(this.bucket.getFileAsString(filenameD)).thenReturn(Optional.of(xml));

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("XXLU000000001");
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itCanReindexFromOneSpecificChangelog() throws ObjectStoreServiceException {
    final String xmlFileName = "XXLU000000001.akn.xml";
    final String xml = LoadXmlUtils.loadXmlAsString(Literature.class, xmlFileName);
    when(this.bucket.getFileAsString(xmlFileName)).thenReturn(Optional.of(xml));

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of(xmlFileName)));
    service.indexChangelog(changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("XXLU000000001");
                  return true;
                }));
  }

  @Test
  void ignoredNoneDependentLiteratureFilesInChangelog() throws ObjectStoreServiceException {
    final String xmlFileName = "XXLU000000001.akn.xml";
    final String xml = LoadXmlUtils.loadXmlAsString(Literature.class, xmlFileName);
    when(this.bucket.getFileAsString(xmlFileName)).thenReturn(Optional.of(xml));

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of(xmlFileName, "TESL000000001.akn.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("XXLU000000001");
                  return true;
                }));
  }

  @Test
  void itCanDeleteFromOneSpecificChangelog() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setDeleted(Sets.newHashSet(Set.of("XXLU000000001.akn.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1)).deleteAllById(Set.of("XXLU000000001"));
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllKeys())
        .thenReturn(
            List.of(
                "XXLU000000001.akn.xml",
                "XXLU000000002.akn.xml",
                "changelogs/2025-03-26T14:13:34.096304815Z-literature.json"));
    assertThat(service.getNumberOfIndexableDocumentsInBucket()).isEqualTo(2);
  }
}
