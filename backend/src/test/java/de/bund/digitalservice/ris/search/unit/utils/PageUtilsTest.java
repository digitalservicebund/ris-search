package de.bund.digitalservice.ris.search.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;

@ExtendWith(MockitoExtension.class)
class PageUtilsTest {

  @Mock private SearchHit<Document> mockSearchHit;

  @Test
  void testConvertSnakeCaseKeysToCamelCase() {
    var expectations =
        Map.of("UPPERCASE", "UPPERCASE", "camelCase", "camelCase", "snake_case", "snakeCase");

    for (var entry : expectations.entrySet()) {
      assertThat(PageUtils.snakeCaseToCamelCase(entry.getKey())).isEqualTo(entry.getValue());
    }
  }
}
