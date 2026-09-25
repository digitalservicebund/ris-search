package de.bund.digitalservice.ris.markdown;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.xsd.DescriptionKey;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionConfiguration;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionParser;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Standalone CLI application that parses the XSD schema documentation and writes the resulting
 * field descriptions to Markdown tables under {@code doc/readme}, one file per document kind.
 */
@SpringBootApplication(
    exclude = {
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
    var path = Paths.get(System.getProperty("user.dir") + "/../doc/readme", filename);
    writeMarkdownFile(buildMarkdownTable(descriptions), path);
  }

  /**
   * Writes the given Markdown content to {@code path}, creating the file first if it doesn't
   * already exist.
   *
   * @param markdown the file content to write
   * @param path the file to write it to
   */
  static void writeMarkdownFile(String markdown, Path path) {
    try {
      if (Files.notExists(path)) {
        Files.createFile(path);
      }
      Files.writeString(path, markdown);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Builds a Markdown table with one row per description key, listing its German and English
   * documentation (if present) in separate columns.
   *
   * @param descriptions the parsed XSD descriptions to render
   * @return the Markdown table as a string
   */
  static String buildMarkdownTable(List<DescriptionKey> descriptions) {
    Map<String, Map<String, String>> descriptionsByKey = new LinkedHashMap<>();
    descriptions.forEach(
        description ->
            descriptionsByKey
                .computeIfAbsent(description.key(), _ -> new LinkedHashMap<>())
                .put(description.lang(), description.description()));

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("| Key | Description DE | Description EN |")
        .append(System.lineSeparator());
    stringBuilder.append("| --- | --- | --- |").append(System.lineSeparator());
    descriptionsByKey.forEach(
        (key, descriptionsByLang) ->
            stringBuilder
                .append("| ")
                .append(key)
                .append(" | ")
                .append(descriptionsByLang.getOrDefault("de", ""))
                .append(" | ")
                .append(descriptionsByLang.getOrDefault("en", ""))
                .append(" |")
                .append(System.lineSeparator()));

    return stringBuilder.toString();
  }
}
