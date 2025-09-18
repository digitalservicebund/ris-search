package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
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

  String literatureContent =
      """
      <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0">
        <akn:doc name="offene-struktur">
          <akn:meta>
            <akn:identification>
              <akn:FRBRExpression>
                <akn:FRBRalias name="documentNumber" value="TEST000000001" />
              </akn:FRBRExpression>
            </akn:identification>
          </akn:meta>
        </akn:doc>
      </akn:akomaNtoso>
      """
          .stripIndent();

  @BeforeEach()
  void setup() {
    this.service = new IndexLiteratureService(bucket, repo);
  }

  @Test
  void reindexAllIgnoresInvalidFiles() throws ObjectStoreServiceException {
    var filenameA = "TEST000000001.akn.xml";
    var filenameB = "TEST000000002.akn.xml";
    when(this.bucket.getAllKeys()).thenReturn(List.of(filenameA, filenameB));
    when(this.bucket.getFileAsString(filenameA)).thenReturn(Optional.of(literatureContent));
    when(this.bucket.getFileAsString(filenameB)).thenReturn(Optional.of("this will not parse"));

    String startingTimestamp =
        ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST000000001");
                  return true;
                }));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itCanReindexFromOneSpecificChangelog() throws ObjectStoreServiceException {
    when(this.bucket.getFileAsString("TEST000000001.akn.xml"))
        .thenReturn(Optional.of(literatureContent));

    Changelog changelog = new Changelog();
    changelog.setChanged(Sets.newHashSet(List.of("TEST000000001.akn.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1))
        .save(
            argThat(
                arg -> {
                  assertThat(arg.id()).isEqualTo("TEST000000001");
                  return true;
                }));
  }

  @Test
  void itCanDeleteFromOneSpecificChangelog() throws ObjectStoreServiceException {
    Changelog changelog = new Changelog();
    changelog.setDeleted(Sets.newHashSet(Set.of("TEST000000001.akn.xml")));
    service.indexChangelog(changelog);

    verify(repo, times(1)).deleteAllById(Set.of("TEST000000001"));
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllKeys())
        .thenReturn(
            List.of(
                "TEST000000001.akn.xml",
                "TEST000000002.akn.xml",
                "changelogs/2025-03-26T14:13:34.096304815Z-literature.json"));
    assertThat(service.getNumberOfIndexableDocumentsInBucket()).isEqualTo(2);
  }
}
