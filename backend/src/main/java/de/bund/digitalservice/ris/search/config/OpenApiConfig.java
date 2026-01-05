package de.bund.digitalservice.ris.search.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
