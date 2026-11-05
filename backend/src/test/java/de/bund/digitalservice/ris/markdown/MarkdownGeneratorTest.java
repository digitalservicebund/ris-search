package de.bund.digitalservice.ris.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.xsd.GlossaryEntry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownGeneratorTest {

  @TempDir private Path tempDir;

  @Test
  void buildGlossaryTableWritesHeaderForEmptyEntries() {
    assertThat(MarkdownGenerator.buildGlossaryTable(List.of()))
        .isEqualTo(
            "| Key | Description DE | Description EN |"
                + System.lineSeparator()
                + "| --- | --- | --- |"
                + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableRendersGermanAndEnglishAsSeparateColumnsInOneRow() {
    var entries = List.of(new GlossaryEntry("ris:tatbestand", "Tatbestand", "Facts"));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .contains("| ris:tatbestand | Tatbestand<br> | Facts<br> |" + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableLeavesAMissingLanguageBlankRatherThanInsertingABr() {
    var entries = List.of(new GlossaryEntry("ris:tatbestand", "Tatbestand", ""));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .contains("| ris:tatbestand | Tatbestand<br> |  |" + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableRendersOneRowPerEntryInTheGivenOrder() {
    var entries =
        List.of(
            new GlossaryEntry("ris:aktenzeichen", "Aktenzeichen", ""),
            new GlossaryEntry("ris:titel", "Titel", ""));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .endsWith(
            "| ris:aktenzeichen | Aktenzeichen<br> |  |"
                + System.lineSeparator()
                + "| ris:titel | Titel<br> |  |"
                + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableKeepsMultiLineDescriptionsOnOneRow() {
    var entries = List.of(new GlossaryEntry("ris:aktenzeichen", "Zeile 1\nZeile 2", ""));

    var table = MarkdownGenerator.buildGlossaryTable(entries);

    assertThat(table)
        .contains("| ris:aktenzeichen | Zeile 1<br>Zeile 2<br> |  |" + System.lineSeparator())
        .doesNotContain("Zeile 1\n");
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
