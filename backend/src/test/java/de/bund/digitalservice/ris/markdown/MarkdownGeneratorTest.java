package de.bund.digitalservice.ris.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MarkdownGeneratorTest {

  @Test
  void toTableCellHtmlConvertsPlainLinesToBrTerminatedLines() {
    assertThat(MarkdownGenerator.toTableCellHtml("Tatbestand")).isEqualTo("Tatbestand<br>");
    assertThat(MarkdownGenerator.toTableCellHtml("Zeile 1\nZeile 2"))
        .isEqualTo("Zeile 1<br>Zeile 2<br>");
  }

  @Test
  void toTableCellHtmlWrapsStarredLinesInAList() {
    assertThat(MarkdownGenerator.toTableCellHtml("* Eins\n* Zwei"))
        .isEqualTo("<ul><li>Eins</li><li>Zwei</li></ul>");
  }

  @Test
  void toTableCellHtmlClosesAListEvenWhenItIsTheLastLine() {
    assertThat(MarkdownGenerator.toTableCellHtml("Intro:\n* Eins\n* Zwei"))
        .isEqualTo("Intro:<br><ul><li>Eins</li><li>Zwei</li></ul>");
  }

  @Test
  void toTableCellHtmlReturnsToPlainLinesAfterAList() {
    assertThat(MarkdownGenerator.toTableCellHtml("* Eins\n* Zwei\nEnde"))
        .isEqualTo("<ul><li>Eins</li><li>Zwei</li></ul>Ende<br>");
  }

  @Test
  void toTableCellHtmlPreservesABlankLineBetweenIntroAndList() {
    assertThat(MarkdownGenerator.toTableCellHtml("Intro:\n\n* Eins"))
        .isEqualTo("Intro:<br><br><ul><li>Eins</li></ul>");
  }
}
