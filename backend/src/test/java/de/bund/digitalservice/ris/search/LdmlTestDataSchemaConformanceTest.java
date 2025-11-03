package de.bund.digitalservice.ris.search;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LdmlTestDataSchemaConformanceTest {

  private static final List<String> NORM_SCHEMA_METADATA_FILES =
      List.of(
          "Grammatiken/Norms/legalDocML.de-metadaten-ris.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-regelungstext.xsd",
          "Grammatiken/Norms/legalDocML.de-metadaten-rechtsetzungsdokument.xsd");

  private static final List<String> LITERATURE_SCHEMA_METADATA_FILES =
      List.of("Grammatiken/Literature/akomantoso30.xsd");

  private List<String> schemaFilesForNorm(String ldmlFile) {
    List<String> schemaFiles = new ArrayList<>(NORM_SCHEMA_METADATA_FILES);
    if (ldmlFile.contains("anlage-regelungstext")) {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-offenestruktur.xsd");
    } else if (ldmlFile.contains("rechtsetzungsdokument")) {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-rechtsetzungsdokument.xsd");
    } else {
      schemaFiles.add("Grammatiken/Norms/legalDocML.de-regelungstextverkuendungsfassung.xsd");
    }

    return schemaFiles;
  }

  private List<String> schemaFilesForLiterature(String ldmlFile) {
    List<String> schemaFiles = new ArrayList<>(LITERATURE_SCHEMA_METADATA_FILES);
    if (ldmlFile.substring(2, 4).equals("LU")) {
      schemaFiles.add("Grammatiken/Literature/ldml-ris-literature-unselbstaendig-meta.xsd");
    } else {
      schemaFiles.add("Grammatiken/Literature/ldml-ris-literature-selbstaendig-meta.xsd");
    }

    return schemaFiles;
  }

  private static Stream<Arguments> testDataPathForDirectory(String directory) throws IOException {
    Path directoryPath = Paths.get(System.getProperty("user.dir")).resolve(directory);
    return Files.walk(directoryPath)
        .map(
            path -> {
              if (!path.toFile().isDirectory() && path.toString().endsWith(".xml")) {
                return Arguments.of(path);
              }

              return null;
            })
        .filter(Objects::nonNull);
  }

  private void assertSchemaValid(Path ldmlFilePath, List<String> schamaFilePaths) {
    try {
      ClassLoader classLoader = LdmlTestDataSchemaConformanceTest.class.getClassLoader();
      List<StreamSource> schemaSources =
          schamaFilePaths.stream()
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
      String ldmlFileURL = ldmlFilePath.toUri().toURL().toExternalForm();
      Source ldmlFileStream = new StreamSource(ldmlFileURL);

      validator.validate(ldmlFileStream);
    } catch (Exception e) {
      Assertions.fail("File: " + ldmlFilePath + " is not schema conform: \n" + e.getMessage());
    }
  }

  private void assertNormIsSchemaValid(Path ldmlFilePath) {
    var schemaFiles = schemaFilesForNorm(ldmlFilePath.toString());
    assertSchemaValid(ldmlFilePath, schemaFiles);
  }

  private void assertLiteratureIsSchemaValid(Path ldmlFilePath) {
    var schemaFiles = schemaFilesForLiterature(ldmlFilePath.toString());
    assertSchemaValid(ldmlFilePath, schemaFiles);
  }

  private static Stream<Arguments> argumentsForNormE2EDataSchemaConformance() throws IOException {
    return testDataPathForDirectory("e2e-data/norm/eli/");
  }

  @ParameterizedTest
  @MethodSource("argumentsForNormE2EDataSchemaConformance")
  void normLdmlE2ETestDataShouldBeSchemaConform(Path ldmlFilePath) {
    assertNormIsSchemaValid(ldmlFilePath);
  }

  private static Stream<Arguments> argumentsForNormIntegrationTestDataSchemaConformance()
      throws IOException {
    return testDataPathForDirectory("src/test/resources/data/LDML/norm/eli");
  }

  @ParameterizedTest
  @MethodSource("argumentsForNormIntegrationTestDataSchemaConformance")
  void normLdmlIntegrationTestDataShouldBeSchemaConform(Path ldmlFilePath) {
    assertNormIsSchemaValid(ldmlFilePath);
  }

  private static Stream<Arguments> argumentsForLiteratureE2EDataSchemaConformance()
      throws IOException {
    return testDataPathForDirectory("e2e-data/literature/");
  }

  @ParameterizedTest
  @MethodSource("argumentsForLiteratureE2EDataSchemaConformance")
  void literatureLdmlE2ETestDataShouldBeSchemaConform(Path ldmlFilePath) {
    assertLiteratureIsSchemaValid(ldmlFilePath);
  }
}
