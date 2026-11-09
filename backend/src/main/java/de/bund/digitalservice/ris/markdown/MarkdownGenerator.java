package de.bund.digitalservice.ris.markdown;

import de.bund.digitalservice.ris.search.xsd.GlossaryEntry;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionConfiguration;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionParser;
import de.bund.digitalservice.ris.search.xsd.XSDDescriptionProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Standalone CLI application that parses the {@code ris} namespace's XSD schema documentation and
 * writes a combined glossary of its field descriptions to a Markdown table under {@code
 * doc/readme}.
 */
@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
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
    var path = Paths.get(System.getProperty("user.dir") + "/../doc/readme", "glossary.md");
    writeMarkdownFile(buildGlossaryTable(parser.getGlossaryEntries()), path);
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
   * Builds a Markdown table with one row per glossary entry, in the given order. The parser emits
   * at most one entry per key: a key with conflicting descriptions is left out of the glossary (see
   * {@link XSDDescriptionParser}), so every key appears in at most one row.
   *
   * @param entries the glossary entries to render
   * @return the Markdown table as a string
   */
  static String buildGlossaryTable(List<GlossaryEntry> entries) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("| Key | Description DE | Description EN |")
        .append(System.lineSeparator());
    stringBuilder.append("| --- | --- | --- |").append(System.lineSeparator());
    entries.forEach(entry ->
        stringBuilder
            .append("| ")
            .append(entry.key())
            .append(" | ")
            .append(toTableCellHtmlOrBlank(entry.descriptionDe()))
            .append(" | ")
            .append(toTableCellHtmlOrBlank(entry.descriptionEn()))
            .append(" |")
            .append(System.lineSeparator()));

    return stringBuilder.toString();
  }

  /**
   * Same as {@link #toTableCellHtml(String)}, but returns a blank cell for a blank description
   * instead of turning it into a stray {@code <br>}.
   */
  private static String toTableCellHtmlOrBlank(String description) {
    return description.isEmpty() ? "" : toTableCellHtml(description);
  }

  /**
   * Converts the raw, multi-line XSD documentation text (plain lines, with {@code *}-prefixed lines
   * forming a bullet list) into HTML that renders correctly inside a single Markdown table cell: a
   * Markdown table row can't contain a literal line break, so each line is terminated with {@code
   * <br>} instead, and consecutive {@code *}-prefixed lines are wrapped in a {@code <ul>}.
   *
   * @param description the raw XSD documentation text
   * @return the description as one line of HTML, safe to embed in a table cell
   */
  static String toTableCellHtml(String description) {
    StringBuilder stringBuilder = new StringBuilder();
    var lines = description.split("\n");
    var listOpen = false;
    for (String line : lines) {
      if (line.trim().startsWith("*")) {
        if (!listOpen) {
          stringBuilder.append("<ul>");
          listOpen = true;
        }
        stringBuilder.append("<li>").append(line.trim().substring(1).trim()).append("</li>");
      } else {
        if (listOpen) {
          stringBuilder.append("</ul>");
          listOpen = false;
        }
        stringBuilder.append(line.trim()).append("<br>");
      }
    }
    if (listOpen) {
      stringBuilder.append("</ul>");
    }
    return stringBuilder.toString();
  }
}
