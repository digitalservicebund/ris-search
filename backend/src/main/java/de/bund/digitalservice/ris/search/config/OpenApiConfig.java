package de.bund.digitalservice.ris.search.config;

import de.bund.digitalservice.ris.search.schema.JsonldType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.AnnotatedTypeScanner;

/** Class to configure OpenAPI */
@Configuration
public class OpenApiConfig {

  @Value("${swagger.server.url}")
  private String url;

  @Value("${swagger.server.description}")
  private String description;

  /**
   * Method to configure OpenAPI
   *
   * @return The OpenAPI object
   */
  @Bean
  public OpenAPI customOpenAPI() {
    Server server = new Server();
    server.setUrl(url);
    server.setDescription(description);

    Contact contact = new Contact();
    contact.setName("DigitalService GmbH des Bundes");
    contact.setUrl("https://digitalservice.bund.de");

    Info info = new Info().title("RIS API").version(ApiConfig.VERSION).contact(contact);

    return new OpenAPI().info(info).servers(List.of(server));
  }

  /**
   * This method is responsible for customizing the OpenAPI specification by adding JSON-LD type
   * information to certain schema definitions. It scans for classes with the JsonldType annotation,
   * and extracts the value. For all schema elements that have one of the found classes as a prefix,
   * e.g. CollectionSchemaSearchMemberSchemaAbstractDocumentSchema matching CollectionSchema, it
   * will take the annotation value ("hydra:Collection") and add it to the response under @type.
   *
   * @return an OpenAPI customizer which is used by Spring Boot to customize the OpenAPI
   *     specification at startup.
   */
  @Bean
  public OpenApiCustomizer openApiCustomizer() {

    var scanner = new AnnotatedTypeScanner(false, JsonldType.class);
    var classes = scanner.findTypes("de.bund.digitalservice.ris.search");

    var jsonLdTypes = new HashMap<String, String>();
    classes.forEach(
        clazz -> {
          var annotation = clazz.getDeclaredAnnotation(JsonldType.class);
          if (annotation != null) {
            var name = clazz.getSimpleName();
            var value = annotation.value();
            jsonLdTypes.put(name, value);
          }
        });

    return openApi ->
        openApi
            .getComponents()
            .getSchemas()
            .forEach(
                (name, schema) -> {
                  var match = jsonLdTypes.keySet().stream().filter(name::startsWith).findFirst();
                  match.ifPresent(
                      m -> {
                        String value = jsonLdTypes.get(m);
                        schema.addProperty("@type", new StringSchema().example(value));
                      });
                });
  }
}
