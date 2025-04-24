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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSystemObjectStorage implements ObjectStorage {

  private static final List<String> possibleProjectRoots = Arrays.asList("ris-search", "workspace");

  private Path localStorageDirectory;

  private final String bucket;

  private static final Logger LOGGER = LogManager.getLogger(FileSystemObjectStorage.class);

  public FileSystemObjectStorage(String bucket, String relativeLocalStorageDirectory) {
    this.bucket = bucket;

    try {
      this.localStorageDirectory = findProjectRoot().resolve(relativeLocalStorageDirectory);
      Files.createDirectories(localStorageDirectory.resolve(bucket));
    } catch (IOException e) {
      LOGGER.error("Couldn't setup local objectstorage: {}", e.getMessage(), e);
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
  public List<String> getAllFilenames() {
    return getAllFilenamesByPath("");
  }

  @Override
  public List<String> getAllFilenamesByPath(String prefix) {
    Path path = Paths.get(localStorageDirectory.toString(), bucket, prefix);
    try (Stream<Path> stream = Files.walk(path)) {
      return stream
          .filter(Files::isRegularFile)
          .map(p -> p.toString().substring(p.toString().indexOf(prefix)))
          .toList();
    } catch (IOException e) {
      throw new FileTransformationException(String.format("Could not list files in %s", path));
    }
  }

  @Override
  public Logger getLogger() {
    return FileSystemObjectStorage.LOGGER;
  }

  @Override
  public FilterInputStream getStream(String objectKey) throws FileNotFoundException {
    File file =
        localStorageDirectory
            .resolve(this.localStorageDirectory + "/" + bucket + "/" + objectKey)
            .toFile();
    return new DataInputStream(new FileInputStream(file));
  }

  @Override
  public void delete(String fileName) {
    int numPaths = fileName.split("/").length;

    try {
      Path absolutePath =
          localStorageDirectory.resolve(Path.of(bucket, fileName)).toFile().toPath();
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

  @Override
  public void save(String fileName, String fileContent) {

    File bucketDirectory = new File(localStorageDirectory.toAbsolutePath().toString(), bucket);
    File file = new File(bucketDirectory, fileName);

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
}
