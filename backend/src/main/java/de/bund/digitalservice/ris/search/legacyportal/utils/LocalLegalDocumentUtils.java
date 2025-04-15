package de.bund.digitalservice.ris.search.legacyportal.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class LocalLegalDocumentUtils {

  private static final Logger logger = LogManager.getLogger(LocalLegalDocumentUtils.class);
  private static final String PATH_LEGAL_DOCUMENTS_NORMS_1_4 =
      "legacyportal/legalDocumentsNorms1_4";
  public static final Resource LDML_1_4_FOLDER =
      new ClassPathResource(PATH_LEGAL_DOCUMENTS_NORMS_1_4);
  private static final String PATH_LEGAL_DOCUMENTS_NORMS_1_6 =
      "legacyportal/legalDocumentsNorms1_6";
  public static final Resource LDML_1_6_FOLDER =
      new ClassPathResource(PATH_LEGAL_DOCUMENTS_NORMS_1_6);

  private LocalLegalDocumentUtils() {}

  public static Optional<List<String>> getFiles(Resource ldmlFolder) {
    try {
      return Optional.of(FileUtils.findFilesWithEnding(ldmlFolder.getFile(), ".xml"));
    } catch (IOException e) {
      logger.error(
          String.format("Couldn't get files by the directory %s", ldmlFolder.getFilename()));
      return Optional.empty();
    }
  }

  public static Optional<byte[]> getFile(Resource ldmlFolder, String file) {
    try (FileInputStream fl = new FileInputStream(ldmlFolder.getFile() + "/" + file)) {
      return Optional.of(fl.readAllBytes());
    } catch (IOException e) {
      var errorMessage =
          String.format("Couldn't get file by the path %s / %s", ldmlFolder.getFilename(), file);
      logger.error(errorMessage);
      return Optional.empty();
    }
  }
}
