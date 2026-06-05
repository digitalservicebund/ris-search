package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Service for creating and managing bulk exports of objects from ObjectStorage. */
public class BulkExportService implements Job {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  private final ObjectStorage sourceBucket;
  private final ObjectStorage destinationBucket;
  private final String outputName;
  private final String prefix;
  private final Predicate<String> keyFilter;

  /**
   * Job to include potentially all files from a source bucket in a zip file and store it in a
   * destination bucket.
   *
   * @param sourceBucket the ObjectStorage bucket to read files from
   * @param destinationBucket the ObjectStorage bucket to upload the ZIP archive to
   * @param outputName the base name for the output ZIP file
   * @param prefix the prefix to filter objects in the sourceBucket
   * @param keyFilter filter to exclude files based on their keys
   */
  public BulkExportService(
      ObjectStorage sourceBucket,
      ObjectStorage destinationBucket,
      String outputName,
      String prefix,
      Predicate<String> keyFilter) {
    this.sourceBucket = sourceBucket;
    this.destinationBucket = destinationBucket;
    this.outputName = outputName;
    this.prefix = prefix;
    this.keyFilter = keyFilter;
  }

  /**
   * Creates a ZIP archive of all objects in the sourceBucket with the given prefix, uploads it to
   * the destinationBucket, and deletes obsolete archives.
   *
   * @return ReturnCode
   */
  public ReturnCode runJob() {

    String timestamp = Instant.now().toString();
    List<String> keysToZip =
        sourceBucket.getAllKeysByPrefix(prefix).stream().filter(keyFilter).toList();

    if (keysToZip.isEmpty()) {
      logger.error("No files found for bucket {}", sourceBucket.getClass());
      return ReturnCode.ERROR;
    }

    String affectedPrefix = "snapshots/" + outputName;
    String resultObjectKey = affectedPrefix + "_" + timestamp + ".zip";
    List<String> obsoleteObjectKeys = destinationBucket.getAllKeysByPrefix(affectedPrefix);

    // Setup the pipe with a larger buffer (5MB) to reduce thread context-switching
    try (PipedInputStream pipedInputStream = new PipedInputStream(1024 * 1024 * 5);
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        ExecutorService executorService = Executors.newFixedThreadPool(1)) {

      // Background Task: Run the ZIP engine on the thread pool
      Future<?> zipFuture =
          executorService.submit(
              () -> {
                // Wrap in BufferedOutputStream to prevent tiny, slow writes to the pipe
                try (OutputStream bufferedOut = new BufferedOutputStream(pipedOutputStream)) {
                  ZipManager.writeZipArchive(sourceBucket, keysToZip, bufferedOut);
                } catch (IOException e) {
                  logger.error("Error inside ZIP background thread.", e);
                  // trigger ExecutionException when retrieving result of zip thread
                  throw new UncheckedIOException(e);
                }
              });

      // Stream the data directly into the destination bucket
      // putStream blocks here and pulls data out of the pipe as the background thread pushes it in
      long byteCount = destinationBucket.putStream(resultObjectKey, pipedInputStream);

      // Ensure the background thread finished completely and check for errors
      zipFuture.get();

      logger.log(
          Level.INFO,
          () ->
              "Added %s items to %s, compressed size: %s"
                  .formatted(
                      keysToZip.size(),
                      resultObjectKey,
                      FileUtils.byteCountToDisplaySize(byteCount)));

    } catch (InterruptedException e) {
      logger.error("Job execution was interrupted", e);
      Thread.currentThread().interrupt();
      return ReturnCode.ERROR;

    } catch (ExecutionException | IOException e) {
      logger.error("Error processing key '{}'", keysToZip, e);
      return ReturnCode.ERROR;
    }

    // Clean up old backups now that the new one is safely uploaded
    logger.info("deleting obsolete archive objects {}", obsoleteObjectKeys);
    for (String obsoleteObjectKey : obsoleteObjectKeys) {
      destinationBucket.delete(obsoleteObjectKey);
    }

    return ReturnCode.SUCCESS;
  }
}
