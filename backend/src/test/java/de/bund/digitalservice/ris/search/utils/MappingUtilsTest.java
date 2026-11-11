package de.bund.digitalservice.ris.search.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MappingUtilsTest {

  @Test
  @DisplayName("mapList applies the mapper to every element in order")
  void mapListMapsEveryElement() {
    assertThat(MappingUtils.mapList(List.of("a", "bb", "ccc"), String::length))
        .containsExactly(1, 2, 3);
  }

  @Test
  @DisplayName("mapList returns an empty list for an empty source")
  void mapListReturnsEmptyListForEmptySource() {
    assertThat(MappingUtils.mapList(List.<String>of(), String::length)).isEmpty();
  }

  @Test
  @DisplayName("mapList returns null for a null source")
  void mapListReturnsNullForNullSource() {
    assertThat(MappingUtils.mapList(null, String::length)).isNull();
  }
}
