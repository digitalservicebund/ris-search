package de.bund.digitalservice.ris.markdown;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.xsd.DescriptionKey;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionConfiguration;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionParser;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    WebMvcAutoConfiguration.class
})
@Import(XSDDescriptionConfiguration.class)
@EnableConfigurationProperties({XSDDescriptionProperties.class})
public class MarkdownGenerator implements CommandLineRunner {

  private final XSDDescriptionParser parser;

  public MarkdownGenerator(XSDDescriptionParser parser) {
    this.parser = parser;
  }

  public static void main(String[] args) {
    new SpringApplicationBuilder(MarkdownGenerator.class).web(WebApplicationType.NONE).run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    generateMarkdown(parser.getDescriptions(DocumentKind.CASE_LAW), "caselaw.md");
    generateMarkdown(parser.getDescriptions(DocumentKind.ADMINISTRATIVE_DIRECTIVE), "adm.md");
    generateMarkdown(parser.getDescriptions(DocumentKind.LITERATURE), "literature.md");
  }

  private void generateMarkdown(List<DescriptionKey> descriptions, String filename) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("| Key | Language | Description |").append(System.lineSeparator());
    stringBuilder.append("| --- | --- | --- |").append(System.lineSeparator());
    descriptions.forEach(description ->
      stringBuilder.append("| ").append(description.key()).append(" | ")
          .append(description.lang()).append(" | ").append(description.description()).append(" |")
          .append(System.lineSeparator())
    );

    try {
      var path = Paths.get(System.getProperty("user.dir") + "/../doc/readme", filename);
      if (Files.notExists(path)) {
        Files.createFile(path);
      }
      Files.writeString(path, stringBuilder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
