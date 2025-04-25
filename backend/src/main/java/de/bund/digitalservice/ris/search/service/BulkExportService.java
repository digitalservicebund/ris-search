package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.caselawhandover.shared.S3Bucket;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class BulkExportService {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  public void updateExportAsync(
      S3Bucket bucket, S3Bucket destinationBucket, String outputName, String prefix) {
    try {
      this.updateExport(bucket, destinationBucket, outputName, prefix);
    } catch (IOException e) {
      logger.error("in async operation", e);
    }
  }

  public void updateExport(
      S3Bucket bucket, S3Bucket destinationBucket, String outputName, String prefix)
      throws IOException {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd_HH-mm-ss"));
    List<String> keysToZip = bucket.getAllFilenamesByPath(prefix);

    String resultObjectKey = "archive/" + outputName + "_" + timestamp + ".zip";

    // Create a PipedInputStream and PipedOutputStream for streaming data
    PipedInputStream pipedInputStream = new PipedInputStream();
    PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);

    // Create an ExecutorService for parallel execution
    try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
      Future<?> uploadFuture = null; // future for the upload
      // Start the multipart upload in a separate thread
      uploadFuture =
          executorService.submit(
              () -> {
                try {
                  return destinationBucket.putStream(resultObjectKey, pipedInputStream);
                } catch (RuntimeException e) {
                  pipedOutputStream
                      .close(); // prevent the writing thread from waiting to write more data
                  throw e;
                }
              });

      // Create the zip archive in the main thread, writing to the PipedOutputStream
      try (ZipOutputStream zos = new ZipOutputStream(pipedOutputStream)) {
        for (String key : keysToZip) {
          logger.info("Zipping key {}", key);

          InputStream inputStream = bucket.getStream(key);
          ZipEntry zipEntry = new ZipEntry(key);
          zos.putNextEntry(zipEntry);

          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            zos.write(buffer, 0, bytesRead);
          }
          inputStream.close();
          zos.closeEntry();
        }
        zos.finish();
        zos.flush();
      } catch (Exception e) {
        // need to close the pipedOutputStream here, otherwise the upload thread might hang
        pipedOutputStream.close();
        throw e;
      }

      // Wait for the upload to complete and get the result
      try {
        String resultEtag = (String) uploadFuture.get(); // blocking call, waits for the upload
        logger.info("Upload completed: {}, {}", resultObjectKey, resultEtag);
      } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
        throw new IOException("Error during upload: " + e.getMessage(), e); // unwrap
      }

    } catch (Exception e) {
      logger.error("Error processing key '{}'", keysToZip, e);
    }
    // shut down the executor
    // Close the PipedOutputStream in a finally block
    // Log the error, but don't throw an exception here.  We want to ensure
    // the upload thread is also cleaned up.

    // TODO on success, delete old archive
  }
}
