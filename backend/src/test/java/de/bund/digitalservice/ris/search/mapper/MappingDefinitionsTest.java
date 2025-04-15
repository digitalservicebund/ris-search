package de.bund.digitalservice.ris.search.mapper;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MappingDefinitionsTest {

  private static Stream<Arguments> argumentsFor_getOpenSearchName() {
    return Stream.of(
        Arguments.of("caseLawOpenSearchToSchemaMap", MappingDefinitions.ResolutionMode.ALL),
        Arguments.of("normsOpenSearchToSchemaMap", MappingDefinitions.ResolutionMode.ALL),
        Arguments.of("caseLawOpenSearchToSchemaMap", MappingDefinitions.ResolutionMode.CASE_LAW),
        Arguments.of("normsOpenSearchToSchemaMap", MappingDefinitions.ResolutionMode.NORMS));
  }

  @ParameterizedTest
  @MethodSource("argumentsFor_getOpenSearchName")
  void test_getOpenSearchName(String source, MappingDefinitions.ResolutionMode mode) {
    var sourceFields =
        switch (source) {
          case "caseLawOpenSearchToSchemaMap" -> MappingDefinitions.caseLawOpenSearchToSchemaMap;
          case "normsOpenSearchToSchemaMap" -> MappingDefinitions.normsOpenSearchToSchemaMap;
          default -> null;
        };
    for (Map.Entry<String, String> entry : Objects.requireNonNull(sourceFields).entrySet()) {
      String openSearchField = entry.getKey();
      String schemaField = entry.getValue();
      Assertions.assertEquals(
          openSearchField, MappingDefinitions.getOpenSearchName(schemaField, mode));
    }
  }

  private static Stream<Arguments> argumentsFor_getOpenSearchName_nonExisting() {
    return Stream.of(
        Arguments.of(MappingDefinitions.ResolutionMode.ALL),
        Arguments.of(MappingDefinitions.ResolutionMode.CASE_LAW),
        Arguments.of(MappingDefinitions.ResolutionMode.NORMS));
  }

  @ParameterizedTest
  @MethodSource("argumentsFor_getOpenSearchName_nonExisting")
  void test_getOpenSearchName_nonExisting(MappingDefinitions.ResolutionMode mode) {
    Assertions.assertNull(MappingDefinitions.getOpenSearchName("non_existing", mode));
  }
}
