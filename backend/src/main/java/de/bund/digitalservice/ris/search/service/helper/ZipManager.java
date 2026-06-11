package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Utility class for creating zip archives from S3 objects. */
public class ZipManager {
  private ZipManager() {}

  private static final Logger logger = LogManager.getLogger(ZipManager.class);

  /**
   * Writes a zip archive containing the objects from the given S3 keys to the provided output
   * stream.
   *
   * @param s3Bucket The S3 bucket from which to retrieve the objects.
   * @param keys A list of S3 keys representing the objects to be included in the zip archive.
   * @param outputStream The output stream where the zip archive will be written.
   * @throws IOException If an I/O error occurs during the process.
   */
  public static void writeZipArchive(
      ObjectStorage s3Bucket, List<String> keys, OutputStream outputStream) throws IOException {

    int numKeys = keys.size();
    logger.info("writing zip archive");
    try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
      int i = 1;
      for (String key : keys) {
        appendZipEntryFromS3(s3Bucket, key, zipOut);
        logger.info("written file {} of {}", i, numKeys);
        i++;
      }
    } catch (IOException e) {
      throw new IOException("error building ZipOutputStream with keys %s".formatted(keys), e);
    }
  }

  private static void appendZipEntryFromS3(
      ObjectStorage s3Bucket, String key, ZipOutputStream zipOut) throws IOException {
    ZipEntry zipEntry = new ZipEntry(key);
    zipOut.putNextEntry(zipEntry);

    try (InputStream objectInputStream = s3Bucket.getStream(key)) {
      byte[] bytes = new byte[4096];
      int length;
      while ((length = objectInputStream.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
    } catch (IOException | NoSuchKeyException e) {
      throw new IOException("error adding item with key %s".formatted(key), e);
    }
  }
}
