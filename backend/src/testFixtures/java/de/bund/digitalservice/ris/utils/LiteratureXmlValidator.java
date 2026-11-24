package de.bund.digitalservice.ris.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

/** Validates Literature LegalDocML XML content/files against the relevant XSD schemas. */
public class LiteratureXmlValidator {

  private static final String SLI_SCHEMA = "schema/literature-sli.xsd";
  private static final String ULI_SCHEMA = "schema/literature-uli.xsd";

  private static List<String> schemaFilesForLiterature(String ldmlFilePath) {
    String fileName = Path.of(ldmlFilePath).getFileName().toString();
    return List.of(fileName.startsWith("LU", 2) ? ULI_SCHEMA : SLI_SCHEMA);
  }

  public static void validateFile(Path xmlFilePath) throws MalformedURLException {
    XmlValidator.validateXmlFile(xmlFilePath, schemaFilesForLiterature(xmlFilePath.toString()));
  }
}
