package de.bund.digitalservice.ris.search.legacyportal.utils;

import de.bund.digitalservice.ris.search.legacyportal.exceptions.FileTransformationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

  private FileUtils() {}

  public static List<String> findFilesWithEnding(File directory, String ending) {
    Stream<Path> stream = null;
    try {
      stream = Files.walk(Paths.get(directory.getPath()));
      return stream
          .map(Object::toString)
          .filter(f -> f.endsWith(ending))
          .map(
              s -> {
                Path path = Paths.get(s);
                Path directoryPath = Paths.get(directory.getPath());
                Path subpath = path.subpath(directoryPath.getNameCount(), path.getNameCount());
                return subpath.toString();
              })
          .toList();
    } catch (IOException | NullPointerException e) {
      throw new FileTransformationException("No files could be found.");
    } finally {
      if (stream != null) {
        stream.close();
      }
    }
  }
}
