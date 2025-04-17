package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
      LOGGER.error("Couldn't setup local objectstorage: {}", e.getMessage(), e);
    }
  }

  protected Path findProjectRoot() {
    File currentDir = new File(System.getProperty("user.dir"));
    while (!possibleProjectRoots.contains(currentDir.getName())) {
      currentDir = currentDir.getParentFile();
    }
    return currentDir.toPath();
  }

  @Override
  public List<String> getAllFilenamesByPath(String prefix) {
    Path path = Paths.get(localStorageDirectory.toString(), bucket, prefix);
    try (Stream<Path> stream = Files.walk(path)) {
      return stream
          .filter(Files::isRegularFile)
          .map(p -> p.toString().substring(p.toString().indexOf(prefix)))
          .toList();
    } catch (NoSuchFileException e) {
      return new ArrayList<>();
    } catch (IOException e) {
      throw new FileTransformationException(String.format("Could not list files in %s", path));
    }
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
  public void close() {}

  @Override
  public void delete(String fileName) {
    Path file = localStorageDirectory.resolve(bucket + "/" + fileName).toFile().toPath();
    try {
      Files.delete(file);
    } catch (IOException e) {
      LOGGER.error("Couldn't delete object from local storage: {}", e.getMessage(), e);
    }
  }
}
