package de.bund.digitalservice.ris.utils;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/** Validates XML content or files against a given set of XSD schema files. */
public class XmlValidator {

  private static List<StreamSource> loadSchemaFiles(List<String> schemaFilePaths) {

    ClassLoader classLoader = NormXmlValidator.class.getClassLoader();

    return schemaFilePaths.stream()
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
  }

  /**
   * Validates the given XML string against the given schema files.
   *
   * @param xmlContent the XML content to validate
   * @param schemaFilePaths classpath locations of the XSD schema files to validate against
   */
  public static void validateXmlContent(String xmlContent, List<String> schemaFilePaths) {
    Source ldmlFileStream = new StreamSource(new StringReader(xmlContent));
    validate(ldmlFileStream, schemaFilePaths);
  }

  /**
   * Validates the XML file at the given path against the given schema files.
   *
   * @param filePath path of the XML file to validate
   * @param schemaFilePaths classpath locations of the XSD schema files to validate against
   * @throws MalformedURLException if the file path cannot be converted to a URL
   */
  public static void validateXmlFile(Path filePath, List<String> schemaFilePaths)
      throws MalformedURLException {
    String ldmlFileURL = filePath.toUri().toURL().toExternalForm();
    Source ldmlFileStream = new StreamSource(ldmlFileURL);
    validate(ldmlFileStream, schemaFilePaths);
  }

  private static void validate(Source ldmlFileStream, List<String> schemaFilePaths) {
    try {
      List<StreamSource> schemaSources = loadSchemaFiles(schemaFilePaths);
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(schemaSources.toArray(new StreamSource[0]));
      Validator validator = schema.newValidator();

      validator.validate(ldmlFileStream);
    } catch (Exception e) {
      throw new IllegalArgumentException("File is not schema conform: \n" + e.getMessage());
    }
  }
}
