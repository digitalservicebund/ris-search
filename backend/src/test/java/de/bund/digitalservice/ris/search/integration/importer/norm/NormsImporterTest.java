package de.bund.digitalservice.ris.search.integration.importer.norm;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsSynthesizedRepository;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.NormImportService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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

  @Autowired private NormImportService normsImporter;
  @Autowired private IndexStatusService indexStatusService;
  @Autowired private NormsSynthesizedRepository normIndex;
  @Autowired private NormsBucket normsBucket;
  @Autowired private PortalBucket portalBucket;

  @BeforeEach
  void beforeEach() {
    final Predicate<String> isChangelogOrStatus =
        s -> s.contains("changelog") || s.equals(NormImportService.NORM_STATUS_FILENAME);
    portalBucket.getAllKeys().stream()
        .filter(isChangelogOrStatus)
        .forEach(filename -> portalBucket.delete(filename));

    indexStatusService.saveStatus(NormImportService.NORM_STATUS_FILENAME, getMockState());

    normsBucket.getAllKeys().stream()
        .filter(isChangelogOrStatus)
        .forEach(filename -> normsBucket.delete(filename));
    normIndex.deleteAll();
  }

  @Test
  @DisplayName("Only unprocessed changelogs are considered")
  void testProcessedChangelogIsIgnored() throws ObjectStoreServiceException {
    final String ignoredManifestationEli =
        "eli/bund/bgbl-1/1991/s101/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    final String nonIgnoredManifestationEli =
        "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml";

    Instant now = Instant.now();
    Instant firstChangelogTime = now.minus(2, ChronoUnit.HOURS);
    Instant secondChangelogTime = now.minus(1, ChronoUnit.HOURS);

    // changelog1.json contains Norm 1...
    normsBucket.save(
        "changelogs/%s-changelog.json".formatted(firstChangelogTime.toString()),
        "{\"changed\": [\"%s\"]}".formatted(ignoredManifestationEli));

    // opposite case: verify that the file can actually be imported
    normsBucket.save(
        "changelogs/%s-changelog.json".formatted(secondChangelogTime.toString()),
        "{\"changed\": [\"%s\"]}".formatted(nonIgnoredManifestationEli));

    assertThat(normIndex.count()).isZero();

    IndexingState mockState = getMockState().withLastSuccess(firstChangelogTime.toString());
    normsImporter.importChangelogs(mockState);

    assertThat(normIndex.findAll())
        .map(Norm::getManifestationEliExample)
        .containsExactly(nonIgnoredManifestationEli);
  }

  @Test
  @DisplayName("Updating the manifestation for an expression ELI only keeps the new manifestation")
  void testDeleteAndUpdate() throws ObjectStoreServiceException {
    final String expressionEli = "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/regelungstext-1";

    final String expressionEliUriPart = "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu";
    final String oldManifestationEli = expressionEliUriPart + "/1992-01-01/regelungstext-1.xml";
    final String newManifestationEli = expressionEliUriPart + "/1992-01-02/regelungstext-1.xml";

    normIndex.save(
        Norm.builder().id(expressionEli).manifestationEliExample(oldManifestationEli).build());
    assertThat(normIndex.count()).isEqualTo(1);

    Instant now = Instant.now();
    Instant lastSuccess = now.minus(1, ChronoUnit.HOURS);

    normsBucket.save(
        "changelogs/%s-changelog.json".formatted(now),
        """
            {"changed": ["%s"], "deleted": ["%s"]}"""
            .formatted(newManifestationEli, oldManifestationEli));

    IndexingState mockState = getMockState().withLastSuccess(lastSuccess.toString());

    normsImporter.importChangelogs(mockState);

    assertThat(normIndex.count()).isEqualTo(1);
    assertThat(normIndex.findById(expressionEli).get().getManifestationEliExample())
        .isEqualTo(newManifestationEli);
  }

  @Test
  @DisplayName("Delete removes the norm")
  void testDelete() throws ObjectStoreServiceException {
    final String expressionEliToDelete =
        "eli/bund/bgbl-1/1993/s101/1993-01-01/1/deu/regelungstext-1";
    final String expressionEliToKeep = "eli/bund/bgbl-1/1994/s101/1994-01-01/1/deu/regelungstext-1";

    final String manifestationEliToDelete =
        "eli/bund/bgbl-1/1993/s101/1993-01-01/1/deu/1993-01-01/regelungstext-1.xml";

    List<Norm> initialState = new ArrayList<>();
    initialState.add(Norm.builder().id(expressionEliToDelete).build());
    initialState.add(Norm.builder().id(expressionEliToKeep).build());
    normIndex.saveAll(initialState);

    assertThat(normIndex.count()).isEqualTo(2);

    Instant now = Instant.now();

    normsBucket.save(
        "changelogs/%s-changelog.json".formatted(now),
        """
                    { "deleted": [ "%s" ] }""".formatted(manifestationEliToDelete));

    Instant lastSuccess = now.minus(1, ChronoUnit.HOURS);
    IndexingState mockState = getMockState().withLastSuccess(lastSuccess.toString());

    normsImporter.importChangelogs(mockState);

    IndexingState indexingState =
        indexStatusService.loadStatus(NormImportService.NORM_STATUS_FILENAME);
    assertThat(indexingState.lastSuccess()).isEqualTo(now.toString());
    assertThat(normIndex.count()).isEqualTo(1);
    assertThat(normIndex.findById(expressionEliToKeep)).isPresent();
  }

  private IndexingState getMockState() {
    Instant time = Instant.now();
    return new IndexingState(time.toString(), time.toString(), time.toString(), null, null);
  }
}
