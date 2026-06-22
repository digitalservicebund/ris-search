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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Value;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Service for creating and managing bulk exports of objects from ObjectStorage. */
public class BulkExportService {

  private final Logger logger = LogManager.getLogger(BulkExportService.class);

  private final ObjectStorage sourceBucket;
  private final ObjectStorage destinationBucket;
  private final String prefix;
  private final Predicate<String> keyFilter;
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
    this.prefix = prefix;
    this.keyFilter = keyFilter;
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
        sourceBucket.getAllKeysByPrefix(prefix).stream().filter(keyFilter).toList();

    if (keysToZip.isEmpty()) {
      logger.error("No files found for bucket {}", sourceBucket.getClass());
      return false;
    }

    BlockingQueue<FileData> downloadQueue = new ArrayBlockingQueue<>(20);

    try (PipedInputStream pipedInputStream = new PipedInputStream(1024 * 1024 * 5);
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        ExecutorService orchestrator = Executors.newFixedThreadPool(2)) {

      Runnable downloaderTask = new S3BatchDownloader(sourceBucket, keysToZip, downloadQueue);
      Runnable zipperTask =
          new ZipStreamConsumer(downloadQueue, pipedOutputStream, keysToZip.size());

      Future<?> downloadWorker = orchestrator.submit(downloaderTask);
      Future<?> zipWorker = orchestrator.submit(zipperTask);

      // Main thread blocks here, piping input data directly to S3
      long byteCount = destinationBucket.putStream(resultObjectKey, pipedInputStream);

      // Check background worker health
      downloadWorker.get();
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

    // Clean up old backups exclusively on success
    deleteArchives(obsoleteObjectKeys);
    return true;
  }

  @Value
  static class FileData {
    String key;
    byte[] bytes;

    public static final FileData END_OF_STREAM = new FileData("SIGNAL_SUCCESS", null);
    public static final FileData STREAM_ERROR = new FileData("SIGNAL_FAILURE", null);
  }

  private static final class S3BatchDownloader implements Runnable {
    private final Logger log = LogManager.getLogger(S3BatchDownloader.class);

    private final ObjectStorage sourceBucket;
    private final List<String> keysToDownload;
    private final BlockingQueue<FileData> outputQueue;
    private final Semaphore throttle = new Semaphore(20);

    public S3BatchDownloader(
        ObjectStorage sourceBucket,
        List<String> keysToDownload,
        BlockingQueue<FileData> outputQueue) {
      this.sourceBucket = sourceBucket;
      this.keysToDownload = keysToDownload;
      this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
      try {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
          for (String key : keysToDownload) {
            throttle.acquire();
            pool.submit(
                () -> {
                  try {
                    Optional<byte[]> fileBytes = sourceBucket.get(key);

                    if (fileBytes.isEmpty()) {
                      log.error("Fail-Fast triggered: Object key '{}' not found in storage.", key);
                      outputQueue.put(FileData.STREAM_ERROR);
                      return;
                    }

                    outputQueue.put(new FileData(key, fileBytes.get()));

                  } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                  } catch (Exception e) {
                    log.error("Unexpected download failure for key '{}'", key, e);

                    try {
                      outputQueue.put(FileData.STREAM_ERROR);
                    } catch (InterruptedException ex) {
                      Thread.currentThread().interrupt();
                    }
                  } finally {
                    throttle.release();
                  }
                });
          }
        }

        outputQueue.put(FileData.END_OF_STREAM);

      } catch (InterruptedException e) {
        log.warn("Downloader orchestrator execution was interrupted.");
        Thread.currentThread().interrupt();
      }
    }
  }

  private static final class ZipStreamConsumer implements Runnable {
    private final BlockingQueue<FileData> inputQueue;
    private final OutputStream outputPipe;
    private final int totalFileCount;

    private final Logger logger = LogManager.getLogger(ZipStreamConsumer.class);

    public ZipStreamConsumer(
        BlockingQueue<FileData> inputQueue, OutputStream outputPipe, int totalFileCount) {
      this.inputQueue = inputQueue;
      this.outputPipe = outputPipe;
      this.totalFileCount = totalFileCount;
    }

    @Override
    public void run() {
      int processedCount = 0;

      try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputPipe))) {
        while (true) {
          FileData fileData = inputQueue.take();

          if (fileData == FileData.END_OF_STREAM) {
            break;
          }
          if (fileData == FileData.STREAM_ERROR) {
            throw new IOException(
                "Aborting archive compilation due to an upstream download failure.");
          }

          ZipEntry entry = new ZipEntry(fileData.getKey());
          zos.putNextEntry(entry);
          zos.write(fileData.getBytes());
          zos.closeEntry();

          processedCount++;
          if (processedCount % 10000 == 0 || processedCount == totalFileCount) {
            logger.info(
                "Bulk export progress: {}/{} files packaged for upload",
                processedCount,
                totalFileCount);
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        throw new UncheckedIOException(
            new IOException("ZIP production engine encountered a failure status", e));
      }
    }
  }

  /** Delete every archive of the given prefix */
  public void deleteArchives() {
    logger.info("delete all archives for document Type");
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
