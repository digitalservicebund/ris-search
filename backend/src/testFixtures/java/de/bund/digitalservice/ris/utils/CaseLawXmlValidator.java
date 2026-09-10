package de.bund.digitalservice.ris.utils;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;

/** Validates Case Law LegalDocML XML content/files against the relevant RIS XSD schema. */
public class CaseLawXmlValidator {

  /**
   * The kind of case law document being validated, determining which schema to apply. Implements
   * {@link Consumer} so a {@code Type} can be passed directly wherever an XML-validating callback
   * is expected, e.g. {@code getXmlFromTemplateWithValidation(context, Type.DECISION)}.
   */
  public enum Type implements Consumer<String> {
    DECISION("schema/caselaw-decision.xsd"),
    PENDING_PROCEEDING("schema/caselaw-pending-proceeding.xsd");

    @Getter private final List<String> schemaFiles;

    Type(String path) {
      schemaFiles = List.of(path);
    }

    @Override
    public void accept(String xmlContent) {
      validateContent(xmlContent, this);
    }
  }

  private CaseLawXmlValidator() {}

  public static void validateContent(String xmlContent, Type type) {
    XmlValidator.validateXmlContent(xmlContent, type.getSchemaFiles());
  }

  public static void validateFile(Path xmlFilePath, Type type) throws MalformedURLException {
    XmlValidator.validateXmlFile(xmlFilePath, type.getSchemaFiles());
  }
}
