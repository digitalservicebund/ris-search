package de.bund.digitalservice.ris.search.jobs.norm;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NormIndexSyncJobIntegrationTest extends ContainersIntegrationBase {
  // for these test cases, refer to the file structure at resources/data/LDML/norm

  @Autowired private NormIndexSyncJob normsImporter;
  @Autowired private IndexStatusService indexStatusService;

  @BeforeEach
  void beforeEach() {
    cleanup();
  }

  @Test
  @DisplayName("Only unprocessed changelogs are considered")
  void testProcessedChangelogIsIgnored() throws ObjectStoreServiceException {
    // Given 1 norm work with 1 expression and 1 norm work with 2 expressions where in force dates
    // have no overlaps
    final String ignoredEliFile =
        "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    final String nonIgnoredEliFile =
        "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml";
    final String relatedByWorkEliFile =
        "eli/bund/bgbl-1/1992/s101/1992-02-01/2/deu/1992-02-02/regelungstext-1.xml";

    normsBucket.save(
        ignoredEliFile,
        NormTestDataBuilder.builder()
            .eli(ignoredEliFile)
            .inForceDate("2000-01-01")
            .outOfForceDate("2000-01-02")
            .buildNormXml());
    normsBucket.save(
        nonIgnoredEliFile,
        NormTestDataBuilder.builder()
            .eli(nonIgnoredEliFile)
            .inForceDate("2000-01-03")
            .outOfForceDate("2000-01-04")
            .buildNormXml());
    normsBucket.save(
        relatedByWorkEliFile,
        NormTestDataBuilder.builder()
            .eli(relatedByWorkEliFile)
            .inForceDate("2000-01-05")
            .outOfForceDate("2000-01-06")
            .buildNormXml());
    // and GIVEN 1 already processed changelog file and 1 unprocessed changelog file
    indexStatusService.saveStatus(NormIndexSyncJob.NORM_STATUS_FILENAME, getMockState());
    String test1HourAgo = "2024-01-01T11:00:00Z";
    String test2HoursAgo = "2024-01-01T10:00:00Z";
    String firstChangelogFileName = "changelogs/" + test2HoursAgo + "-changelog.json";
    String secondChangelogFileName = "changelogs/" + test1HourAgo + "-changelog.json";
    normsBucket.save(firstChangelogFileName, "{\"changed\": [\"%s\"]}".formatted(ignoredEliFile));
    normsBucket.save(
        secondChangelogFileName, "{\"changed\": [\"%s\"]}".formatted(nonIgnoredEliFile));

    assertThat(normsRepository.count()).isZero();

    // WHEN the importer processes all unprocessed changelog files
    IndexingState mockState = getMockState().withLastProcessedChangelogFile(firstChangelogFileName);
    normsImporter.fetchAndProcessChanges(mockState);

    // THEN only the works in the unprocessed changelog file were processed
    assertThat(normsRepository.findAll())
        .map(Norm::getManifestationEliExample)
        .containsExactlyInAnyOrder(nonIgnoredEliFile, relatedByWorkEliFile);
  }

  @Test
  @DisplayName("Deleting a manifestation and adding a new one reindexes the whole work")
  void testDeleteAndUpdate() throws ObjectStoreServiceException {
    // GIVEN 2 manifestations on the same expression
    final String expressionEli = "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu";
    final String oldManifestationEli = expressionEli + "/1992-01-01/regelungstext-1.xml";
    final String newManifestationEli = expressionEli + "/1992-01-02/regelungstext-1.xml";

    normsBucket.save(
        oldManifestationEli, NormTestDataBuilder.builder().eli(oldManifestationEli).buildNormXml());

    normsBucket.save(
        newManifestationEli, NormTestDataBuilder.builder().eli(newManifestationEli).buildNormXml());

    String testcurrentTime = "2024-01-01T12:00:00Z";
    String test1HourAgo = "2024-01-01T11:00:00Z";
    // and GIVEN the old manifestation was already indexed
    normsRepository.save(
        Norm.builder()
            .id(expressionEli)
            .manifestationEliExample(oldManifestationEli)
            .indexedAt(test1HourAgo)
            .build());
    assertThat(normsRepository.count()).isEqualTo(1);
    // and GIVEN a changelog file deleting the old manifestation and adding a new manifestation on
    // the same expression
    indexStatusService.saveStatus(NormIndexSyncJob.NORM_STATUS_FILENAME, getMockState());
    normsBucket.save(
        "changelogs/" + testcurrentTime + "-changelog.json",
        "{\"changed\": [\"%s\"], \"deleted\": [\"%s\"]}"
            .formatted(newManifestationEli, oldManifestationEli));

    // WHEN we import unprocessed changes
    IndexingState mockState =
        getMockState()
            .withLastProcessedChangelogFile(ChangelogService.CHANGELOGS_PREFIX + test1HourAgo);
    normsImporter.fetchAndProcessChanges(mockState);

    // THEN the expression in the index was updated
    assertThat(normsRepository.count()).isEqualTo(1);
    assertThat(normsRepository.findById(expressionEli).get().getManifestationEliExample())
        .isEqualTo(newManifestationEli);
  }

  @Test
  @DisplayName("Delete removes the norm")
  void testDelete() throws ObjectStoreServiceException {
    // GIVEN 2 expressions in the repository
    String now = "2024-01-01T12:00:00Z";
    String lastSuccess = "2024-01-01T11:00:00Z";

    final EliFile toKeep =
        EliFile.fromString("eli/bund/bgbl-1/1994/s101/1994-01-01/1/deu/0000-01-01/abc.xml").get();
    final EliFile toDelete =
        EliFile.fromString(
                "eli/bund/bgbl-1/1993/s101/1993-01-01/1/deu/1993-01-01/regelungstext-1.xml")
            .get();

    List<Norm> initialState = new ArrayList<>();
    initialState.add(
        Norm.builder()
            .id(toDelete.getExpressionEliPath().toString())
            .workEli(toDelete.getWorkEliPath().toString())
            .indexedAt(lastSuccess)
            .build());
    initialState.add(
        Norm.builder()
            .id(toKeep.getExpressionEliPath().toString())
            .workEli(toKeep.getWorkEliPath().toString())
            .indexedAt(lastSuccess)
            .build());
    normsRepository.saveAll(initialState);

    assertThat(normsRepository.count()).isEqualTo(2);

    // and GIVEN a changelog file indicating 1 expression should be deleted.
    indexStatusService.saveStatus(NormIndexSyncJob.NORM_STATUS_FILENAME, getMockState());
    String changelogFileName = "changelogs/" + now + "-changelog.json";

    normsBucket.save(changelogFileName, "{ \"deleted\": [ \"%s\" ] }".formatted(toDelete));

    IndexingState mockState =
        getMockState()
            .withLastProcessedChangelogFile(ChangelogService.CHANGELOGS_PREFIX + lastSuccess);

    // WHEN we process the changelog file
    normsImporter.fetchAndProcessChanges(mockState);

    // THEN the file was processed
    IndexingState indexingState =
        indexStatusService.loadStatus(NormIndexSyncJob.NORM_STATUS_FILENAME);
    assertThat(indexingState.lastProcessedChangelogFile()).isEqualTo(changelogFileName);
    // and THEN the correct expression was deleted and the other expression was not deleted
    assertThat(normsRepository.count()).isEqualTo(1);
    assertThat(normsRepository.findById(toKeep.getExpressionEliPath().toString())).isPresent();
  }

  private IndexingState getMockState() {
    Instant time = SharedTestConstants.TIMESTAMP_2024_01_01_AS_INSTANT;
    return new IndexingState(time.toString(), time.toString());
  }
}
