package de.bund.digitalservice.ris.search;

import de.bund.digitalservice.ris.utils.LiteratureXmlValidator;
import de.bund.digitalservice.ris.utils.NormXmlValidator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LdmlTestDataSchemaConformanceTest {

  private NormXmlValidator.Type normSchemaTypeForFile(String ldmlFile) {
    if (ldmlFile.contains("anlage-regelungstext")) {
      return NormXmlValidator.Type.ANLAGE;
    } else if (ldmlFile.contains("rechtsetzungsdokument")) {
      return NormXmlValidator.Type.RECHTSETZUNGSDOKUMENT;
    } else {
      return NormXmlValidator.Type.REGELUNGSTEXT;
    }
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

  private void assertNormIsSchemaValid(Path ldmlFilePath) {
    try {
      NormXmlValidator.validateFile(ldmlFilePath, normSchemaTypeForFile(ldmlFilePath.toString()));
    } catch (Exception e) {
      Assertions.fail("Norm: " + ldmlFilePath + " is not schema conform: \n" + e.getMessage());
    }
  }

  private void assertLiteratureIsSchemaValid(Path ldmlFilePath) {
    try {
      LiteratureXmlValidator.validateFile(ldmlFilePath);
    } catch (Exception e) {
      Assertions.fail(
          "Literature: " + ldmlFilePath + " is not schema conform: \n" + e.getMessage());
    }
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
