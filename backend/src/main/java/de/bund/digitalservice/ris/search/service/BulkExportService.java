package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.caselawhandover.shared.S3Bucket;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BulkExportService {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  @Async
  public void updateExportAsync(
      S3Bucket bucket, S3Bucket destinationBucket, String outputName, String prefix) {
    try {
      this.updateExport(bucket, destinationBucket, outputName, prefix);
    } catch (IOException | ExecutionException e) {
      logger.error("in async operation", e);
    } catch (InterruptedException e) {
      logger.error("in async operation", e);
      Thread.currentThread().interrupt();
    }
  }

  public void updateExport(
      S3Bucket bucket, S3Bucket destinationBucket, String outputName, String prefix)
      throws IOException, ExecutionException, InterruptedException {
    String timestamp = Instant.now().toString();
    List<String> keysToZip = bucket.getAllFilenamesByPath(prefix);

    String affectedPrefix = "archive/" + outputName;
    String resultObjectKey = affectedPrefix + "_" + timestamp + ".zip";
    List<String> obsoleteObjectKeys = destinationBucket.getAllFilenamesByPath(affectedPrefix);

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

          try (InputStream inputStream = bucket.getStream(key)) {
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
      if (logger.isInfoEnabled()) {
        logger.info(
            "Added {} items to {}, compressed size: {}",
            keysToZip.size(),
            resultObjectKey,
            FileUtils.byteCountToDisplaySize(byteCount));
      }

    } catch (InterruptedException | ExecutionException | RuntimeException e) {
      logger.error("Error processing key '{}'", keysToZip, e);
      throw e;
    }

    logger.info("deleting obsolete archive objects {}", obsoleteObjectKeys);
    for (String obsoleteObjectKey : obsoleteObjectKeys) {
      destinationBucket.delete(obsoleteObjectKey);
    }
  }
}
