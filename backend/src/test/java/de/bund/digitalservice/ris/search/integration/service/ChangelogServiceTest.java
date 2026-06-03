package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.SET;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
@Tag("integration")
class ChangelogServiceTest {

  ChangelogService changelogService;

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired PortalBucket bucket;

  @BeforeEach
  void setup() {
    changelogService = new ChangelogService(bucket, objectMapper) {};
  }

  @Test
  void itSkipsInvalidChangelogContent() throws ObjectStoreServiceException {
    bucket.save("fileName", "you shall not parse");
    Optional<Changelog> changelog = changelogService.parseOneChangelog("fileName");
    assertThat(changelog.isEmpty()).isTrue();
  }

  @Test
  void itSkipsEmptyChangelogFiles() throws ObjectStoreServiceException {
    bucket.save("fileName", "");
    Optional<Changelog> changelog = changelogService.parseOneChangelog("fileName");
    assertThat(changelog.isEmpty()).isTrue();
  }

  @Test
  void itReturnsChangelogsSortedByTimestamp() {
    Instant now = Instant.now();
    String lastSuccess =
        ChangelogService.CHANGELOGS_PREFIX
            + now.minus(2, ChronoUnit.HOURS).toString()
            + "-changelog.json";
    String olderChangelogFile =
        ChangelogService.CHANGELOGS_PREFIX + now.minus(3, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile1 =
        ChangelogService.CHANGELOGS_PREFIX + now.plus(1, ChronoUnit.HOURS) + "-changelog.json";
    String changelogFile2 =
        ChangelogService.CHANGELOGS_PREFIX + now.plus(2, ChronoUnit.HOURS) + "-changelog.json";

    bucket.save(olderChangelogFile, "");
    bucket.save(changelogFile1, "");
    bucket.save(changelogFile2, "");

    List<String> changelogs = changelogService.getNewChangelogsPaths(lastSuccess);

    assertThat(changelogs)
        .asInstanceOf(LIST)
        .containsExactlyInAnyOrderElementsOf(List.of(changelogFile1, changelogFile2));
  }

  @Test
  void itDetectsChangeAllChangelogs() throws JsonProcessingException {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file")));
    Changelog log2 = new Changelog();
    log2.setChangeAll(true);
    Changelog log3 = new Changelog();
    log3.setDeleted(new HashSet<>(List.of("deleted")));

    bucket.save("log1", objectMapper.writeValueAsString(log1));
    bucket.save("log2", objectMapper.writeValueAsString(log2));
    bucket.save("log3", objectMapper.writeValueAsString(log3));

    Changelog result = changelogService.getChangesFromFiles(List.of("log1", "log2", "log3"));
    assertThat(result.isChangeAll()).isTrue();
  }

  @Test
  void itMergesAListOfChangelogs() throws JsonProcessingException {
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("obsolete", "changed")));
    log1.setDeleted(new HashSet<>(List.of("obsolete")));
    Changelog log2 = new Changelog();
    log2.setChanged(new HashSet<>(List.of("changed2", "obsolete2")));
    log2.setDeleted(new HashSet<>(List.of("deleted")));
    Changelog log3 = new Changelog();
    log3.setChanged(new HashSet<>(List.of("changed3")));
    log3.setDeleted(new HashSet<>(List.of("obsolete2", "deleted2")));

    bucket.save("log1", objectMapper.writeValueAsString(log1));
    bucket.save("log2", objectMapper.writeValueAsString(log2));
    bucket.save("log3", objectMapper.writeValueAsString(log3));

    var result = changelogService.getChangesFromFiles(List.of("log1", "log2", "log3"));

    assertThat(result.getChanged())
        .asInstanceOf(SET)
        .containsExactlyInAnyOrder("changed", "changed2", "changed3");

    assertThat(result.getDeleted())
        .asInstanceOf(SET)
        .containsExactlyInAnyOrder("deleted", "deleted2", "obsolete", "obsolete2");
  }

  @Test
  void itCollectsChangesBetweenTimestamps()
      throws JsonProcessingException, ObjectStoreServiceException {
    Instant from = Instant.parse("2026-07-03T12:00:00Z");
    Instant to = Instant.parse("2027-01-01T12:00:00Z");

    Changelog expectedFirst =
        new Changelog(new HashSet<>(List.of("file1")), new HashSet<>(), false);
    Changelog expectedSecond =
        new Changelog(new HashSet<>(List.of("file2")), new HashSet<>(), false);

    bucket.save("changelogs/2026-07-03T11:00:00.000000Z-norm.json", "this one is ignored");
    bucket.save(
        "changelogs/2026-07-03T12:00:00.933434Z-norm.json",
        objectMapper.writeValueAsString(expectedFirst));
    bucket.save(
        "changelogs/2026-07-08T12:00:00.933434Z-norm.json",
        objectMapper.writeValueAsString(expectedSecond));

    Changelog result = changelogService.getChangesBetween(from, to);

    assertThat(result.getChanged()).asInstanceOf(SET).containsExactlyInAnyOrder("file1", "file2");
  }

  @Test
  void itDetectsChangeAllBetweenTimestamps()
      throws JsonProcessingException, ObjectStoreServiceException {
    Instant from = Instant.parse("2026-07-03T12:00:00Z");
    Instant to = Instant.parse("2027-01-01T12:00:00Z");

    Changelog expectedFirst = new Changelog(new HashSet<>(), new HashSet<>(), true);
    Changelog expectedSecond =
        new Changelog(new HashSet<>(List.of("file2")), new HashSet<>(), false);

    bucket.save(
        "changelogs/2026-07-03T12:00:00.000000Z-norm.json",
        objectMapper.writeValueAsString(expectedFirst));
    bucket.save(
        "changelogs/2027-01-01T12:00:00.000000Z-norm.json",
        objectMapper.writeValueAsString(expectedSecond));

    Changelog result = changelogService.getChangesBetween(from, to);

    assertThat(result.isChangeAll()).isTrue();
  }
}
