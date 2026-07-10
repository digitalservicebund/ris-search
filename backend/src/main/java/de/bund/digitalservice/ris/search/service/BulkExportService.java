package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Service for creating and managing bulk exports of objects from ObjectStorage. */
public class BulkExportService {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  private final ObjectStorage sourceBucket;
  private final ObjectStorage destinationBucket;
  public static final String BULK_ZIP_PREFIX = "snapshots/";
  public static final String JOB_STATE_STORAGE_PREFIX = "snapshot-job-state/";

  private final String archivePrefix;

  /**
   * Service to include potentially all files from a source bucket in a zip file and store it in a
   * destination bucket.
   *
   * @param sourceBucket the ObjectStorage bucket to read files from
   * @param destinationBucket the ObjectStorage bucket to upload the ZIP archive to
   * @param outputName the base name for the output ZIP file
   */
  public BulkExportService(
      ObjectStorage sourceBucket, ObjectStorage destinationBucket, String outputName) {
    this.sourceBucket = sourceBucket;
    this.destinationBucket = destinationBucket;
    this.archivePrefix = BULK_ZIP_PREFIX + outputName;
  }

  /**
   * starts the archiving process
   *
   * @param timestamp start of the snapshot creation
   * @return true if successful, false on error
   */
  public boolean updateLatestZip(Instant timestamp) {
    String resultObjectKey = archivePrefix + "_" + timestamp + ".zip";
    // collect already existing archive to be deleted after a successful snapshot or a detected file
    // deletion
    List<String> obsoleteObjectKeys = destinationBucket.getAllKeysByPrefix(archivePrefix);

    logger.info("Creating snapshot");
    List<String> keysToZip =
        sourceBucket.getAllKeys().stream()
            .filter(key -> !key.startsWith(ChangelogService.CHANGELOGS_PREFIX))
            .toList();

    if (keysToZip.isEmpty()) {
      logger.error("No files found for bucket {}", sourceBucket.getClass());
      return false;
    }

    try (ExecutorService executor = Executors.newSingleThreadExecutor();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 1024 * 5);
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream)) {

      Future<Void> zipWorker =
          CompletableFuture.runAsync(
              new ZipStreamProducer(keysToZip, sourceBucket, pipedOutputStream), executor);

      // Main thread blocks here, piping input data directly to S3. S3ObjectStorageClient::putStream
      // uses a ReadableByteChannel which, unlike InputStream, listens for interruptions and
      // gracefully cancels the multipart upload.
      long byteCount = destinationBucket.putStream(resultObjectKey, pipedInputStream);

      // Check background worker health
      zipWorker.get();

      logger.log(
          Level.INFO,
          () ->
              "Added %s items to %s, compressed size: %s"
                  .formatted(
                      keysToZip.size(),
                      resultObjectKey,
                      FileUtils.byteCountToDisplaySize(byteCount)));

    } catch (InterruptedException e) {
      logger.error("Bulk export execution was interrupted.", e);
      Thread.currentThread().interrupt();
      return false;
    } catch (Exception e) {
      logger.error("Bulk export execution failed or was aborted due to structural error.", e);
      return false;
    }

    // Clean up old backups exclusively on success. If deletion fails (and throws an exception) the
    // job will fail. This is wanted. We will successfully have a zip, but will get an error to
    // investigate.
    deleteArchives(obsoleteObjectKeys);
    return true;
  }

  private record FetchResult(String key, Optional<byte[]> bytes) {}

  private static final class ZipStreamProducer implements Runnable {
    private final List<String> keysToDownload;
    private final OutputStream outputPipe;
    private final int totalFileCount;
    private final ObjectStorage sourceBucket;

    private final Logger log = LogManager.getLogger(ZipStreamProducer.class);

    public ZipStreamProducer(
        List<String> keysToDownload, ObjectStorage sourceBucket, OutputStream outputPipe) {
      this.keysToDownload = keysToDownload;
      this.sourceBucket = sourceBucket;
      this.outputPipe = outputPipe;
      this.totalFileCount = keysToDownload.size();
    }

    public void run() {
      int processedCount = 0;

      // do not rely on the autoClosable of the ExecutorService to be able to force close on Error
      ExecutorService downloadExecutor = Executors.newVirtualThreadPerTaskExecutor();
      try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputPipe))) {
        int maxConcurrentDownloads = 30;
        int submittedCount = 0;

        CompletionService<FetchResult> completionService =
            new ExecutorCompletionService<>(downloadExecutor);

        // preload the queue of concurrent file downloads
        int itemsToPreload = Math.min(maxConcurrentDownloads, totalFileCount);
        for (int i = 0; i < itemsToPreload; i++) {
          String key = keysToDownload.get(i);
          completionService.submit(() -> new FetchResult(key, sourceBucket.get(key)));
          submittedCount++;
        }

        for (int i = 0; i < totalFileCount; i++) {
          // Pull the next available completed file
          FetchResult result = completionService.take().get(5, TimeUnit.MINUTES);

          Optional<byte[]> bytesOption = result.bytes;
          if (bytesOption.isEmpty()) {
            throw new IOException("Object key not found: " + result.key());
          }

          // Write to ZIP
          ZipEntry entry = new ZipEntry(result.key());
          zos.putNextEntry(entry);
          zos.write(bytesOption.get());
          zos.closeEntry();
          processedCount++;

          // Immediately feed a new file into the pipeline to maintain maximum active downloads
          if (submittedCount < totalFileCount) {
            String nextKey = keysToDownload.get(submittedCount++);
            completionService.submit(() -> new FetchResult(nextKey, sourceBucket.get(nextKey)));
          }

          if (processedCount % 10000 == 0 || processedCount == totalFileCount) {
            log.info("Bulk export progress: {}/{} files packaged", processedCount, totalFileCount);
          }
        }
      } catch (Exception e) {
        // force close the executor in case of an error
        downloadExecutor.shutdownNow();

        if (e instanceof InterruptedException) {
          log.error("Download thread was interrupted.", e);
          Thread.currentThread().interrupt();
        }

        throw new UncheckedIOException(
            new IOException("ZIP production engine encountered a failure status", e));
      } finally {
        downloadExecutor.close();
      }
    }
  }

  /** Delete all archives for the document kind this service manages */
  public void deleteArchives() {
    logger.info("deleting all archives for prefix: {}", archivePrefix);
    List<String> files = destinationBucket.getAllKeysByPrefix(archivePrefix);
    deleteArchives(files);
  }

  private void deleteArchives(List<String> obsoleteObjectKeys) {
    logger.info("Deleting archive objects {}", obsoleteObjectKeys);
    for (String obsoleteObjectKey : obsoleteObjectKeys) {
      destinationBucket.delete(obsoleteObjectKey);
    }
  }
}
