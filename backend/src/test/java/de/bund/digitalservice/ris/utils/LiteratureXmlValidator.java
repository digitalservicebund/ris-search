package de.bund.digitalservice.ris.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Validates Literature LegalDocML XML content/files against the relevant XSD schemas. */
public class LiteratureXmlValidator {

  private static final List<String> LITERATURE_SCHEMA_METADATA_FILES =
      List.of("Grammatiken/Literature/akomantoso30.xsd");

  private static List<String> schemaFilesForLiterature(String ldmlFilePath) {
    List<String> schemaFiles = new ArrayList<>(LITERATURE_SCHEMA_METADATA_FILES);
    if (ldmlFilePath.startsWith("LU", 2)) {
      schemaFiles.add("Grammatiken/Literature/ldml-ris-literature-unselbstaendig-meta.xsd");
    } else {
      schemaFiles.add("Grammatiken/Literature/ldml-ris-literature-selbstaendig-meta.xsd");
    }

    return schemaFiles;
  }

  public static void validateFile(Path xmlFilePath) throws MalformedURLException {
    XmlValidator.validateXmlFile(xmlFilePath, schemaFilesForLiterature(xmlFilePath.toString()));
  }
}
