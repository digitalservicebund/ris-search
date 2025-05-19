package de.bund.digitalservice.ris;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.jetbrains.annotations.NotNull;

public class ZipTestUtils {
  /**
   * Reads an InputStream containing a generated ZIP.
   *
   * @return Map of entry names and bytes
   */
  public static @NotNull Map<String, byte[]> readZipStream(InputStream inputStream)
      throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream(inputStream);

    Map<String, byte[]> files = new HashMap<>();

    ZipEntry entry;
    while ((entry = zipInputStream.getNextEntry()) != null) {
      files.put(entry.getName(), zipInputStream.readAllBytes());
      zipInputStream.closeEntry();
    }
    zipInputStream.close();
    return files;
  }
}
