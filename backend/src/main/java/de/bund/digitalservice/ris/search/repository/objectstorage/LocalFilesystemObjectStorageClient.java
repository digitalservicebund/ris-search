package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFilesystemObjectStorageClient implements ObjectStorageClient {

  private static final List<String> possibleProjectRoots = Arrays.asList("ris-search", "workspace");

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalFilesystemObjectStorageClient.class);

  private Path localStorageDirectory;

  private final String bucket;

  public LocalFilesystemObjectStorageClient(String bucket, String relativeLocalStorageDirectory) {
    this.bucket = bucket;

    try {
      this.localStorageDirectory = findProjectRoot().resolve(relativeLocalStorageDirectory);
      Files.createDirectories(localStorageDirectory.resolve(bucket));
    } catch (IOException e) {
      LOGGER.error("Couldn't setup local object storage: {}", e.getMessage(), e);
    }
  }

  private Path findProjectRoot() {
    File currentDir = new File(System.getProperty("user.dir"));
    while (!possibleProjectRoots.contains(currentDir.getName())) {
      currentDir = currentDir.getParentFile();
    }
    return currentDir.toPath();
  }

  @Override
  public List<String> getAllFilenamesByPath(String prefix) {
    Path bucketPath = localStorageDirectory.resolve(bucket);
    Path basePath = bucketPath.resolve(prefix);
    try (Stream<Path> stream = Files.walk(basePath)) {
      return stream
          .filter(Files::isRegularFile)
          .map(p -> p.toString().substring(bucketPath.toString().length() + 1))
          .toList();
    } catch (IOException e) {
      throw new FileTransformationException("Could not list files in " + basePath, e);
    }
  }

  @Override
  public FilterInputStream getStream(String objectKey) throws FileNotFoundException {
    File file = localStorageDirectory.resolve(bucket).resolve(objectKey).toFile();
    return new DataInputStream(new FileInputStream(file));
  }

  @Override
  public void save(String fileName, String fileContent) {

    File file = localStorageDirectory.resolve(bucket).resolve(fileName).toFile();

    try {
      FileUtils.writeByteArrayToFile(file, fileContent.getBytes());
    } catch (IOException e) {
      LOGGER.error(e.toString());
    }
  }

  @Override
  public void close() {
    // Nothing to close in the Filesystem implementation
  }

  /**
   * @param fileName String
   *     <p>delete a specific file and all parts of the prefix when it contains directories
   */
  @Override
  public void delete(String fileName) {
    int numPaths = fileName.split("/").length;

    try {
      Path absolutePath = localStorageDirectory.resolve(bucket).resolve(fileName);
      for (int i = 0; i < numPaths; i++) {
        File f = new File(absolutePath.toString());
        if (f.isFile() || (f.isDirectory() && Objects.requireNonNull(f.list()).length == 0)) {
          Files.delete(absolutePath);
        }
        absolutePath = absolutePath.getParent();
      }
    } catch (NullPointerException | IOException e) {
      LOGGER.error("Couldn't delete object from local storage: {}", e.getMessage(), e);
    }
  }
}
