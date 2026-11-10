package de.bund.digitalservice.ris.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.xsd.TypeDocumentation;
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
            "| Key | Description DE | Description EN | Schema file |"
                + System.lineSeparator()
                + "| --- | --- | --- | --- |"
                + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableRendersGermanAndEnglishAsSeparateColumnsInOneRow() {
    var entries =
        List.of(new TypeDocumentation("ris:tatbestand", "Tatbestand", "Facts", "ris.xsd"));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .contains(
            "| ris:tatbestand | Tatbestand<br> | Facts<br> | ris.xsd |" + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableLeavesAMissingLanguageBlankRatherThanInsertingABr() {
    var entries = List.of(new TypeDocumentation("ris:tatbestand", "Tatbestand", "", "ris.xsd"));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .contains("| ris:tatbestand | Tatbestand<br> |  | ris.xsd |" + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableRendersOneRowPerEntryInTheGivenOrder() {
    var entries =
        List.of(
            new TypeDocumentation("ris:aktenzeichen", "Aktenzeichen", "", "ris.xsd"),
            new TypeDocumentation("ris:titel", "Titel", "", "ris.xsd"));

    assertThat(MarkdownGenerator.buildGlossaryTable(entries))
        .endsWith(
            "| ris:aktenzeichen | Aktenzeichen<br> |  | ris.xsd |"
                + System.lineSeparator()
                + "| ris:titel | Titel<br> |  | ris.xsd |"
                + System.lineSeparator());
  }

  @Test
  void buildGlossaryTableKeepsMultiLineDescriptionsOnOneRow() {
    var entries =
        List.of(new TypeDocumentation("ris:aktenzeichen", "Zeile 1\nZeile 2", "", "ris.xsd"));

    var table = MarkdownGenerator.buildGlossaryTable(entries);

    assertThat(table)
        .contains(
            "| ris:aktenzeichen | Zeile 1<br>Zeile 2<br> |  | ris.xsd |" + System.lineSeparator())
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
