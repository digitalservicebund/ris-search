package de.bund.digitalservice.ris.search.xsd;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springdoc.core.converters.PropertyCustomizingConverter;
import org.springframework.core.io.DefaultResourceLoader;

class XSDPropertyCustomizerTest {

  record ExampleSchema(
      @RISSchema(name = "ris:verfasserListe", requiredMode = RequiredMode.REQUIRED)
          List<String> verfasser,
      @RISSchema(name = "ris:ausgabe") String ausgabe,
      @Schema(requiredMode = RequiredMode.REQUIRED) String literaturtyp) {}

  @Test
  void addsRequiredRisSchemaPropertiesToTheRequiredListOfTheParent() {
    var parser =
        new XSDDescriptionParser(new XSDDescriptionProperties(), new DefaultResourceLoader());
    var converters = new ModelConverters();
    converters.addConverter(
        new PropertyCustomizingConverter(Optional.of(List.of(new XSDPropertyCustomizer(parser)))));

    io.swagger.v3.oas.models.media.Schema<?> schema =
        converters.resolveAsResolvedSchema(new AnnotatedType(ExampleSchema.class)).schema;

    assertThat(schema.getRequired()).containsExactly("literaturtyp", "verfasser");
  }
}
