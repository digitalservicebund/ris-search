package de.bund.digitalservice.ris.search.xsd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import groovy.util.logging.Slf4j;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, SoftAssertionsExtension.class})
@Slf4j
class XSDDescriptionParserTest {

  private static final Logger log = LoggerFactory.getLogger(XSDDescriptionParserTest.class);
  @MockitoBean ResourceLoader context;

  @InjectSoftAssertions
  private SoftAssertions softAssertions;

  @Test
  void testCaselaw() {
    XSDDescriptionProperties properties = new XSDDescriptionProperties();
    properties.getXsdLocations().put("caselaw", new String[] { "schema/caselaw-decision.xsd", "schema/caselaw-pending-proceeding.xsd" });
    properties.setSchemaPrefix("schema/");

    when(context.getResource(anyString())).thenAnswer(invocation ->
        new ClassPathResource(invocation.getArgument(0),
            XSDDescriptionParser.class.getClassLoader())
    );

    var parser = new XSDDescriptionParser(properties, context);

    assertThat(parser.getDescriptions(DocumentKind.CASE_LAW)).hasSize(85);
  }

  @Test
  void testAdm() {
    XSDDescriptionProperties properties = new XSDDescriptionProperties();
    properties.getXsdLocations().put("adm", new String[] { "schema/adm.xsd" });
    properties.setSchemaPrefix("schema/");

    when(context.getResource(anyString())).thenAnswer(invocation ->
        new ClassPathResource(invocation.getArgument(0),
            XSDDescriptionParser.class.getClassLoader())
    );

    var parser = new XSDDescriptionParser(properties, context);

    assertThat(parser.getDescriptions(DocumentKind.ADMINISTRATIVE_DIRECTIVE)).hasSize(85);
  }

  @Test
  void testLiterature() {
    XSDDescriptionProperties properties = new XSDDescriptionProperties();
    properties.getXsdLocations().put("literature", new String[] { "schema/literature-sli.xsd", "schema/literature-uli.xsd" });
    properties.setSchemaPrefix("schema/");

    when(context.getResource(anyString())).thenAnswer(invocation ->
        new ClassPathResource(invocation.getArgument(0),
            XSDDescriptionParser.class.getClassLoader())
    );

    var parser = new XSDDescriptionParser(properties, context);

    assertThat(parser.getDescriptions(DocumentKind.LITERATURE)).hasSize(88);
  }

  @Test
  void testUsedDescriptions() {
    XSDDescriptionProperties properties = new XSDDescriptionProperties();
    properties.getXsdLocations().put("caselaw", new String[] { "schema/caselaw-decision.xsd", "schema/caselaw-pending-proceeding.xsd" });
    properties.getXsdLocations().put("adm", new String[] { "schema/adm.xsd" });
    properties.getXsdLocations().put("literature", new String[] { "schema/literature-sli.xsd", "schema/literature-uli.xsd" });
    properties.setSchemaPrefix("schema/");

    when(context.getResource(anyString())).thenAnswer(invocation ->
        new ClassPathResource(invocation.getArgument(0),
            XSDDescriptionParser.class.getClassLoader())
    );

    var parser = new XSDDescriptionParser(properties, context);

    Set<RISSchema> schemas = parseRISSchema();

    assertThat(schemas).isNotEmpty();

    schemas.forEach(schema -> {
      softAssertions.assertThat(parser.findDescription(schema.name(), schema.language()))
          .as("no description found for %s and %s", schema.name(), schema.language())
          .isPresent();
    });
  }

  private Set<RISSchema> parseRISSchema() {
    Reflections reflections = new Reflections("de.bund.digitalservice.ris.search", Scanners.FieldsAnnotated);

    Set<Field> annotatedFields = reflections.getFieldsAnnotatedWith(RISSchema.class);

    return annotatedFields.stream()
        .map(field -> field.getDeclaredAnnotation(RISSchema.class))
        .filter(schema -> schema.description().isBlank())
        .collect(Collectors.toSet());
  }
}
