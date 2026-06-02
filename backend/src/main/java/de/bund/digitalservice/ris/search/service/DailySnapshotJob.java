package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;

@Service
public class DailySnapshotJob implements Job {

  ObjectStorage sourceBucket;

  PortalBucket targetBucket;

  public DailySnapshotJob(NormsBucket sourceBucket, PortalBucket targetBucket) {
    this.sourceBucket = sourceBucket;
    this.targetBucket = targetBucket;
  }

  @Override
  public ReturnCode runJob() {
    try {
      PipedInputStream pipedInputStream =
          new PipedInputStream(1024 * 1024 * 5); // 5MB internal buffer
      PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
      List<String> keys =
          sourceBucket.getAllKeysExcludingPrefix(ChangelogService.CHANGELOGS_PREFIX);

      Thread zipThread =
          new Thread(
              () -> {
                try (ZipOutputStream zos =
                    new ZipOutputStream(new BufferedOutputStream(pipedOutputStream))) {

                  for (String key : keys) {
                    try (InputStream s3ObjectStream = sourceBucket.getStream(key)) {
                      zos.putNextEntry(new ZipEntry(key));

                      // Stream bytes directly from source S3 file -> ZIP engine
                      byte[] buffer = new byte[4096];
                      int bytesRead;
                      while ((bytesRead = s3ObjectStream.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                      }
                      zos.closeEntry();

                    } catch (NoSuchKeyException | IOException e) {
                      throw new RuntimeException(e);
                    }
                  }
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });

      zipThread.start();

      targetBucket.putStream("test.zip", pipedInputStream);

      zipThread.join();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return ReturnCode.SUCCESS;
  }
}
