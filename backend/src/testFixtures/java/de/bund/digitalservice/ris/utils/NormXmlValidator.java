package de.bund.digitalservice.ris.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import lombok.Getter;

/** Validates Norm LegalDocML XML content/files against the relevant XSD schemas. */
public class NormXmlValidator {

  /** The kind of norm document being validated, determining which schema to apply. */
  public enum Type {
    REGELUNGSTEXT("Grammatiken/Norms/norm-regelungstext.xsd"),
    ANLAGE("Grammatiken/Norms/norm-offenestruktur.xsd"),
    RECHTSETZUNGSDOKUMENT("Grammatiken/Norms/norm-rechtsetzungsdokument.xsd");

    @Getter private final List<String> schemaFiles;

    Type(String path) {
      schemaFiles =
          List.of(
              "Grammatiken/Norms/norm-metadata.xsd",
              "Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd",
              "Grammatiken/Norms/legalDocML.de-metadaten-rechtsetzungsdokument.xsd",
              path);
    }
  }

  public static void validateContent(String xmlContent, Type type) {
    XmlValidator.validateXmlContent(xmlContent, type.getSchemaFiles());
  }

  public static void validateFile(Path xmlFilePath, Type type) throws MalformedURLException {
    XmlValidator.validateXmlFile(xmlFilePath, type.getSchemaFiles());
  }
}
