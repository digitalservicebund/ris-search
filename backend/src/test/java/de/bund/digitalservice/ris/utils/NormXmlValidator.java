package de.bund.digitalservice.ris.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NormXmlValidator {

  public enum Type {
    REGELUNGSTEXT,
    ANLAGE,
    RECHTSETZUNGSDOKUMENT,
  }

  private static final List<String> NORM_SCHEMA_METADATA_FILES =
      List.of(
          "Grammatiken/Norms/legalDocML.de-metadaten-ris.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-rechtsetzungsdokument.xsd");

  private static List<String> schemaFilesForType(Type type) {
    List<String> schemaFiles = new ArrayList<>(NORM_SCHEMA_METADATA_FILES);

    switch (type) {
      case REGELUNGSTEXT:
        schemaFiles.add("Grammatiken/Norms/legalDocML.de-regelungstextverkuendungsfassung.xsd");
        break;
      case ANLAGE:
        schemaFiles.add("Grammatiken/Norms/legalDocML.de-offenestruktur.xsd");
        break;
      case RECHTSETZUNGSDOKUMENT:
        schemaFiles.add("Grammatiken/Norms/legalDocML.de-rechtsetzungsdokument.xsd");
        break;
    }

    return schemaFiles;
  }

  public static void validateContent(String xmlContent, Type type) {
    XmlValidator.validateXmlContent(xmlContent, schemaFilesForType(type));
  }

  public static void validateFile(Path xmlFilePath, Type type) throws MalformedURLException {
    XmlValidator.validateXmlFile(xmlFilePath, schemaFilesForType(type));
  }
}
