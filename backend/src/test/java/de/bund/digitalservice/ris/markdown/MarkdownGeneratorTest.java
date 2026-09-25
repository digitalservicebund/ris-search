package de.bund.digitalservice.ris.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.xsd.DescriptionKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownGeneratorTest {

  @TempDir private Path tempDir;

  @Test
  void buildMarkdownTableWritesHeaderForEmptyDescriptions() {
    assertThat(MarkdownGenerator.buildMarkdownTable(List.of()))
        .isEqualTo(
            "| Key | Description DE | Description EN |"
                + System.lineSeparator()
                + "| --- | --- | --- |"
                + System.lineSeparator());
  }

  @Test
  void buildMarkdownTableFillsOnlyTheAvailableLanguageColumn() {
    var descriptions =
        List.of(new DescriptionKey("ris:tatbestand", "Tatbestand", "de", DocumentKind.CASE_LAW));

    assertThat(MarkdownGenerator.buildMarkdownTable(descriptions))
        .contains("| ris:tatbestand | Tatbestand |  |" + System.lineSeparator());
  }

  @Test
  void buildMarkdownTableMergesLanguagesIntoOneRow() {
    var descriptions =
        List.of(
            new DescriptionKey("ris:gericht", "Gericht", "de", DocumentKind.CASE_LAW),
            new DescriptionKey("ris:gericht", "Court", "en", DocumentKind.CASE_LAW));

    var table = MarkdownGenerator.buildMarkdownTable(descriptions);

    assertThat(table)
        .contains("| ris:gericht | Gericht | Court |" + System.lineSeparator())
        .containsOnlyOnce("ris:gericht");
  }

  @Test
  void buildMarkdownTableKeepsOneRowPerDistinctKey() {
    var descriptions =
        List.of(
            new DescriptionKey("ris:aktenzeichen", "Aktenzeichen", "de", DocumentKind.CASE_LAW),
            new DescriptionKey("ris:gericht", "Gericht", "de", DocumentKind.CASE_LAW));

    var table = MarkdownGenerator.buildMarkdownTable(descriptions);

    assertThat(table)
        .contains("| ris:aktenzeichen | Aktenzeichen |  |" + System.lineSeparator())
        .contains("| ris:gericht | Gericht |  |" + System.lineSeparator());
  }

  @Test
  void writeMarkdownFileCreatesAndWritesANewFile() throws IOException {
    var path = tempDir.resolve("new.md");

    MarkdownGenerator.writeMarkdownFile("content", path);

    assertThat(Files.readString(path)).isEqualTo("content");
  }

  @Test
  void writeMarkdownFileOverwritesAnExistingFile() throws IOException {
    var path = tempDir.resolve("existing.md");
    Files.writeString(path, "old content");

    MarkdownGenerator.writeMarkdownFile("new content", path);

    assertThat(Files.readString(path)).isEqualTo("new content");
  }
}
