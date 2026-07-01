package de.bund.digitalservice.ris.builder;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XmlValidator {

  private static final List<String> NORM_SCHEMA_METADATA_FILES =
      List.of(
          "Grammatiken/Norms/legalDocML.de-metadaten-ris.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-rechtsetzungsdokument.xsd");

  private static List<String> schemaFilesForNorm(String type) {
    List<String> schemaFiles = new ArrayList<>(NORM_SCHEMA_METADATA_FILES);
    if (type.contains("anlage-regelungstext")) {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-offenestruktur.xsd");
    } else if (type.contains("rechtsetzungsdokument")) {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-rechtsetzungsdokument.xsd");
    } else {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-regelungstextverkuendungsfassung.xsd");
    }

    return schemaFiles;
  }

  // TODO: make type an enum
  public static void validateNormXml(String xml, String type) {
    var schemaFiles = schemaFilesForNorm(type);

    try {
      ClassLoader classLoader = XmlValidator.class.getClassLoader();
      List<StreamSource> schemaSources =
          schemaFiles.stream()
              .map(
                  schema -> {
                    URL schemaUrl = classLoader.getResource(schema);
                    if (schemaUrl != null) {
                      return new StreamSource(schemaUrl.toExternalForm());
                    } else {
                      return null;
                    }
                  })
              .toList();

      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(schemaSources.toArray(new StreamSource[0]));
      Validator validator = schema.newValidator();
      Source ldmlFileStream = new StreamSource(new StringReader(xml));

      validator.validate(ldmlFileStream);
    } catch (Exception e) {
      throw new IllegalArgumentException("File is not schema conform: \n" + e.getMessage());
    }
  }
}
