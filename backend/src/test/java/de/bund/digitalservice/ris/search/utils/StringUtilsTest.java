package de.bund.digitalservice.ris.search.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringUtilsTest {
  @Test
  void stripPrefixRemovesPrefixIfInputStartsWithPrefix() {
    assertThat(StringUtils.stripPrefix("abcdefg", "abc")).isEqualTo("defg");
  }

  @Test
  void returnsInputIfPrefixDoesNotMatch() {
    assertThat(StringUtils.stripPrefix("abcdefg", "123")).isEqualTo("abcdefg");
  }

  @Test
  void returnsInputIfPrefixIsNull() {
    assertThat(StringUtils.stripPrefix("abcdefg", null)).isEqualTo("abcdefg");
  }

  @Test
  void stripHtmlConvertsBrTagsToNewlines() {
    assertThat(StringUtils.stripHtml("Tatbestand<br>")).isEqualTo("Tatbestand");
    assertThat(StringUtils.stripHtml("Zeile 1<br>Zeile 2<br>")).isEqualTo("Zeile 1\nZeile 2");
  }

  @Test
  void stripHtmlConvertsListsToDashedLines() {
    assertThat(StringUtils.stripHtml("<ul><li>Eins</li><li>Zwei</li></ul>"))
        .isEqualTo("- Eins\n- Zwei");
  }

  @Test
  void stripHtmlRemovesRemainingTags() {
    assertThat(StringUtils.stripHtml("<strong>wichtig</strong>")).isEqualTo("wichtig");
  }

  @Test
  void stripHtmlReturnsNullForNullInput() {
    assertThat(StringUtils.stripHtml(null)).isNull();
  }
}
