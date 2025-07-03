package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipManager {
  private ZipManager() {}

  public static void writeZipArchive(
      ObjectStorage s3Bucket, List<String> keys, OutputStream outputStream) throws IOException {
    try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
      for (String key : keys) {
        appendZipEntryFromS3(s3Bucket, key, zipOut);
      }
    } catch (IOException e) {
      throw new IOException("error building ZipOutputStream with keys %s".formatted(keys), e);
    }
  }

  private static void appendZipEntryFromS3(
      ObjectStorage s3Bucket, String key, ZipOutputStream zipOut) throws IOException {
    String suffix = key.substring(key.lastIndexOf('/') + 1);
    ZipEntry zipEntry = new ZipEntry(suffix);
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
