package de.bund.digitalservice.ris.search.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PageUtilsTest {

  @Test
  void testConvertSnakeCaseKeysToCamelCase() {
    var expectations =
        Map.of("UPPERCASE", "UPPERCASE", "camelCase", "camelCase", "snake_case", "snakeCase");

    for (var entry : expectations.entrySet()) {
      assertThat(PageUtils.snakeCaseToCamelCase(entry.getKey())).isEqualTo(entry.getValue());
    }
  }
}
