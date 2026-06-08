package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob.LAST_PROCESSED_CHANGELOG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapWriter;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliSitemapJobTest {

  private static final Clock FIXED_UTC_CLOCK =
      Clock.fixed(
          Instant.parse(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING), ZoneOffset.UTC);
  private static final LocalDate FIXED_DAY = SharedTestConstants.DATE_2024_01_01;

  EcliSitemapJob sitemapJob;
  @Mock EcliSitemapWriter sitemapWriter;
  @Mock PortalBucket portalBucket;
  @Mock EcliCrawlerDocumentService documentService;
  @Mock ChangelogService<CaseLawBucket> changelogService;
  String apiUrl = "frontend/url/";

  @BeforeEach
  void setup() {

    sitemapJob =
        new EcliSitemapJob(
            sitemapWriter,
            portalBucket,
            changelogService,
            documentService,
            apiUrl,
            FIXED_UTC_CLOCK);
  }

  @Test
  void itReturnsSuccessfulIfItRanAlready() {
    LocalDate day = FIXED_DAY;
    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of("2025/01/01"));

    Job.ReturnCode code = sitemapJob.runJob();
    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  @Test
  void itWritesFullDiffOnInitialRun() {
    List<String> changelogPaths = List.of("changelog0.xml", "changelog1.xml");
    LocalDate day = FIXED_DAY;
    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of());
    when(changelogService.getNewChangelogsPaths("0")).thenReturn(changelogPaths);

    Job.ReturnCode code = sitemapJob.runJob();

    verify(documentService).writeFullDiff(apiUrl + "v1/eclicrawler/", day);

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  @Test
  void itWritesIncrementalChangelogDiff() throws ObjectStoreServiceException {
    List<String> changelogPaths = List.of("changelog0.xml");
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file1")));
    LocalDate day = FIXED_DAY;

    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of());
    when(portalBucket.getAllKeysByPrefix(LAST_PROCESSED_CHANGELOG)).thenReturn(List.of("file"));
    when(portalBucket.getFileAsString(LAST_PROCESSED_CHANGELOG))
        .thenReturn(Optional.of("changelog/date"));
    when(changelogService.getNewChangelogsPaths("changelog/date")).thenReturn(changelogPaths);
    when(changelogService.getChangesFromFiles(changelogPaths)).thenReturn(log1);

    Job.ReturnCode code = sitemapJob.runJob();

    verify(documentService)
        .writeFromChangelog(
            eq(apiUrl + "v1/eclicrawler/"),
            eq(day),
            argThat(changelog -> changelog.getChanged().equals(log1.getChanged())));

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }
}
