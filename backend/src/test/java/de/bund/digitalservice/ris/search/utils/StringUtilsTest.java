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
}
