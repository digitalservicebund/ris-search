package de.bund.digitalservice.ris.search.integration.importer.norm;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class NormsImporterTest extends ContainersIntegrationBase {
  // for these test cases, refer to the file structure at resources/data/LDML/norm

  @Autowired private NormIndexSyncJob normsImporter;
  @Autowired private IndexStatusService indexStatusService;

  @BeforeEach
  void beforeEach() {
    resetBuckets();
    clearRepositoryData();
    indexStatusService.saveStatus(NormIndexSyncJob.NORM_STATUS_FILENAME, getMockState());
  }

  @Test
  @DisplayName("Only unprocessed changelogs are considered")
  void testProcessedChangelogIsIgnored() throws ObjectStoreServiceException {
    final String ignoredEliFile =
        "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    final String nonIgnoredEliFile =
        "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml";
    final String relatedByWorkEliFile =
        "eli/bund/bgbl-1/1992/s101/1992-02-01/2/deu/1992-02-02/regelungstext-1.xml";

    Instant now = Instant.now();
    String firstChangelogFileName =
        "changelogs/%s-changelog.json".formatted(now.minus(2, ChronoUnit.HOURS).toString());
    String secondChangelogFileName =
        "changelogs/%s-changelog.json".formatted(now.minus(1, ChronoUnit.HOURS).toString());

    // changelog1.json contains Norm 1...
    normsBucket.save(firstChangelogFileName, "{\"changed\": [\"%s\"]}".formatted(ignoredEliFile));

    // opposite case: verify that the file can actually be imported
    normsBucket.save(
        secondChangelogFileName, "{\"changed\": [\"%s\"]}".formatted(nonIgnoredEliFile));

    assertThat(normsRepository.count()).isZero();

    IndexingState mockState = getMockState().withLastProcessedChangelogFile(firstChangelogFileName);
    normsImporter.fetchAndProcessChanges(mockState);

    assertThat(normsRepository.findAll())
        .map(Norm::getManifestationEliExample)
        .containsExactlyInAnyOrder(nonIgnoredEliFile, relatedByWorkEliFile);
  }

  @Test
  @DisplayName("Deleting a manifestation and adding a new one reindexes the whole work")
  void testDeleteAndUpdate() throws ObjectStoreServiceException {

    final String expressionEli = "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu";
    final String oldManifestationEli = expressionEli + "/1992-01-01/regelungstext-1.xml";
    final String newManifestationEli = expressionEli + "/1992-01-02/regelungstext-1.xml";

    Instant now = Instant.now();
    Instant lastSuccess = now.minus(1, ChronoUnit.HOURS);

    normsRepository.save(
        Norm.builder()
            .id(expressionEli)
            .manifestationEliExample(oldManifestationEli)
            .indexedAt(lastSuccess.toString())
            .build());
    assertThat(normsRepository.count()).isEqualTo(1);

    normsBucket.save(
        "changelogs/%s-changelog.json".formatted(now),
        "{\"changed\": [\"%s\"], \"deleted\": [\"%s\"]}"
            .formatted(newManifestationEli, oldManifestationEli));

    IndexingState mockState =
        getMockState()
            .withLastProcessedChangelogFile(NormIndexSyncJob.CHANGELOGS_PREFIX + lastSuccess);

    normsImporter.fetchAndProcessChanges(mockState);

    assertThat(normsRepository.count()).isEqualTo(2);
    assertThat(normsRepository.findById(expressionEli).get().getManifestationEliExample())
        .isEqualTo(newManifestationEli);
  }

  @Test
  @DisplayName("Delete removes the norm")
  void testDelete() throws ObjectStoreServiceException {

    Instant now = Instant.now();
    Instant lastSuccess = now.minus(1, ChronoUnit.HOURS);

    final EliFile toKeep =
        EliFile.fromString("eli/bund/bgbl-1/1994/s101/1994-01-01/1/deu/0000-01-01/abc.xml").get();
    final EliFile toDelete =
        EliFile.fromString(
                "eli/bund/bgbl-1/1993/s101/1993-01-01/1/deu/1993-01-01/regelungstext-1.xml")
            .get();

    List<Norm> initialState = new ArrayList<>();
    initialState.add(
        Norm.builder()
            .id(toDelete.getExpressionEli().toString())
            .workEli(toDelete.getWorkEli().toString())
            .indexedAt(lastSuccess.toString())
            .build());
    initialState.add(
        Norm.builder()
            .id(toKeep.getExpressionEli().toString())
            .workEli(toKeep.getWorkEli().toString())
            .indexedAt(lastSuccess.toString())
            .build());
    normsRepository.saveAll(initialState);

    assertThat(normsRepository.count()).isEqualTo(2);

    String changelogFileName = "changelogs/%s-changelog.json".formatted(now);

    normsBucket.save(changelogFileName, "{ \"deleted\": [ \"%s\" ] }".formatted(toDelete));

    IndexingState mockState =
        getMockState()
            .withLastProcessedChangelogFile(NormIndexSyncJob.CHANGELOGS_PREFIX + lastSuccess);

    normsImporter.fetchAndProcessChanges(mockState);

    IndexingState indexingState =
        indexStatusService.loadStatus(NormIndexSyncJob.NORM_STATUS_FILENAME);
    assertThat(indexingState.lastProcessedChangelogFile()).isEqualTo(changelogFileName);
    assertThat(normsRepository.count()).isEqualTo(1);
    assertThat(normsRepository.findById(toKeep.getExpressionEli().toString())).isPresent();
  }

  private IndexingState getMockState() {
    Instant time = Instant.now();
    return new IndexingState(time.toString(), time.toString(), time.toString());
  }
}
