package de.bund.digitalservice.ris.search.unit.service.helper;

import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZipManagerTest {

  private ObjectStorage bucketMock;

  @BeforeEach
  public void setUp() {
    bucketMock = Mockito.mock(ObjectStorage.class);
  }

  private FilterInputStream createFilterInputStream(String dataString) {
    return new DataInputStream(new ByteArrayInputStream(dataString.getBytes()));
  }

  private void assertZipEntry(
      ZipEntry entry, ZipInputStream zipInputStream, String expectedName, String expectedContent)
      throws IOException {
    Assertions.assertNotNull(entry, "Zip file should contain " + expectedName);
    Assertions.assertEquals(
        expectedName, entry.getName(), "Zip entry " + expectedName + " name should match.");

    String xmlContent = new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8);
    Assertions.assertEquals(
        expectedContent,
        xmlContent,
        "Unzipped " + expectedName + " content should be identical to original.");
  }

  @Test
  @DisplayName("Writes all files for a given bucket key as zip into output stream")
  void writesAllFilesZippedIntoOutputStream() throws IOException, NoSuchKeyException {
    String keyPrefix = "bar/baz/";
    String xmlFilename = "FOO.xml";
    String pngFilename = "FOO-image.png";

    String xmlContentExpected = "xml-content";
    String pngContentExpected = "png-content";

    when(bucketMock.getStream(keyPrefix + xmlFilename))
        .thenReturn(createFilterInputStream(xmlContentExpected));
    when(bucketMock.getStream(keyPrefix + pngFilename))
        .thenReturn(createFilterInputStream(pngContentExpected));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipManager.writeZipArchive(
        bucketMock, List.of(keyPrefix + xmlFilename, keyPrefix + pngFilename), outputStream);

    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

      ZipEntry xmlEntry = zipInputStream.getNextEntry();
      assertZipEntry(xmlEntry, zipInputStream, xmlFilename, xmlContentExpected);

      ZipEntry pngEntry = zipInputStream.getNextEntry();
      assertZipEntry(pngEntry, zipInputStream, pngFilename, pngContentExpected);

      Assertions.assertNull(
          zipInputStream.getNextEntry(), "Zip file should not contain additional entries.");
    }
  }
}
