package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/** Service for creating and managing bulk exports of objects from ObjectStorage. */
@Service
public class BulkExportService {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  /**
   * Asynchronously creates a ZIP archive of all objects in the sourceBucket with the given prefix,
   * uploads it to the destinationBucket, and deletes obsolete archives.
   *
   * @param sourceBucket the ObjectStorage bucket to read files from
   * @param destinationBucket the ObjectStorage bucket to upload the ZIP archive to
   * @param outputName the base name for the output ZIP file
   * @param prefix the prefix to filter objects in the sourceBucket
   */
  public void updateExportAsync(
      ObjectStorage sourceBucket,
      ObjectStorage destinationBucket,
      String outputName,
      String prefix) {
    try {
      this.updateExport(sourceBucket, destinationBucket, outputName, prefix);
    } catch (IOException | ExecutionException | NoSuchKeyException e) {
      logger.error("in async operation", e);
    } catch (InterruptedException e) {
      logger.error("in async operation", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Creates a ZIP archive of all objects in the sourceBucket with the given prefix, uploads it to
   * the destinationBucket, and deletes obsolete archives.
   *
   * @param sourceBucket the ObjectStorage bucket to read files from
   * @param destinationBucket the ObjectStorage bucket to upload the ZIP archive to
   * @param outputName the base name for the output ZIP file
   * @param prefix the prefix to filter objects in the sourceBucket
   * @throws IOException if an I/O error occurs during processing
   * @throws ExecutionException if an error occurs during the upload process
   * @throws InterruptedException if the thread is interrupted while waiting for upload completion
   * @throws NoSuchKeyException if a specified key does not exist in the sourceBucket
   */
  public void updateExport(
      ObjectStorage sourceBucket, ObjectStorage destinationBucket, String outputName, String prefix)
      throws IOException, ExecutionException, InterruptedException, NoSuchKeyException {

    String timestamp = Instant.now().toString();
    List<String> keysToZip = sourceBucket.getAllKeysByPrefix(prefix);

    String affectedPrefix = "archive/" + outputName;
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
                  logger.error(
                      "Error inside ZIP background thread, closing pipe to unblock reader", e);
                  try {
                    pipedOutputStream.close(); // Safeguard: don't let the reader hang
                  } catch (IOException ignored) {
                  }
                  throw new RuntimeException(e);
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

    } catch (InterruptedException | ExecutionException e) {
      logger.error("Error processing key '{}'", keysToZip, e);
      throw e;
    }

    // Clean up old backups now that the new one is safely uploaded
    logger.info("deleting obsolete archive objects {}", obsoleteObjectKeys);
    for (String obsoleteObjectKey : obsoleteObjectKeys) {
      destinationBucket.delete(obsoleteObjectKey);
    }
  }
}
