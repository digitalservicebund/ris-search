package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
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
  @Async
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

    // Create a PipedInputStream and PipedOutputStream for streaming data, and an ExecutorService
    // for parallel execution
    try (PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        ExecutorService executorService = Executors.newFixedThreadPool(1)) {
      // Start the multipart upload in a separate thread
      Future<?> uploadFuture =
          executorService.submit(
              () -> {
                try {
                  return destinationBucket.putStream(resultObjectKey, pipedInputStream);
                } catch (RuntimeException | IOException e) {
                  logger.error("error in putStream, closing pipe", e);
                  pipedOutputStream
                      .close(); // prevent the writing thread from waiting to write more data
                  throw e;
                }
              });

      // Create the zip archive in the main thread, writing to the PipedOutputStream
      try (ZipOutputStream zos = new ZipOutputStream(pipedOutputStream)) {
        logger.info("adding {} files to ZIP", keysToZip.size());
        for (String key : keysToZip) {
          logger.debug("adding {}", key);

          try (InputStream inputStream = sourceBucket.getStream(key)) {
            ZipEntry zipEntry = new ZipEntry(key);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
              zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
          }
        }
        zos.finish();
        zos.flush();
      }

      long byteCount = (long) uploadFuture.get(); // blocking call, waits for the upload
      logger.log(
          Level.INFO,
          () ->
              "Added %s items to %s, compressed size: %s"
                  .formatted(
                      keysToZip.size(),
                      resultObjectKey,
                      FileUtils.byteCountToDisplaySize(byteCount)));

    } catch (InterruptedException | ExecutionException | RuntimeException | NoSuchKeyException e) {
      logger.error("Error processing key '{}'", keysToZip, e);
      throw e;
    }

    logger.info("deleting obsolete archive objects {}", obsoleteObjectKeys);
    for (String obsoleteObjectKey : obsoleteObjectKeys) {
      destinationBucket.delete(obsoleteObjectKey);
    }
  }
}
