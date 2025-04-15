package de.bund.digitalservice.ris.search.unit.utils;

import static de.bund.digitalservice.ris.search.utils.PageUtils.convertSnakeCaseKeysToCamelCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

class PageUtilsTest {

  @Test
  void testConvertSnakeCaseKeysToCamelCase() {
    var input = Map.of("UPPERCASE", "uc", "camelCase", "cc", "snake_case", "sc");
    var expectedOutput = Map.of("UPPERCASE", "uc", "camelCase", "cc", "snakeCase", "sc");

    assertEquals(expectedOutput, convertSnakeCaseKeysToCamelCase(input));
  }

  @Test
  void testConvertSearchHit_ThrowsIllegalStateException() {
    var mockSearchHit = Mockito.mock(SearchHit.class);
    ElasticsearchConverter mockConverter = Mockito.mock(ElasticsearchConverter.class);
    Configurations mockConfigurations = Mockito.mock(Configurations.class);
    Mockito.when(mockConfigurations.getCaseLawsIndexName()).thenReturn("caselaws");
    Mockito.when(mockConfigurations.getNormsIndexName()).thenReturn("norms");
    Mockito.when(mockSearchHit.getIndex()).thenReturn("unexpectedIndex");

    PageUtils instance = new PageUtils(mockConfigurations);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> instance.convertSearchHit(mockSearchHit, mockConverter));

    assertEquals("Unexpected value: unexpectedIndex", exception.getMessage());
  }

  @Test
  void testConvertSearchHit_ConvertsDocumentsFromSubIndices() {
    var mockSearchHit = Mockito.mock(SearchHit.class);
    Mockito.when(mockSearchHit.getIndex()).thenReturn("norms_2025");

    ElasticsearchConverter mockConverter = Mockito.mock(ElasticsearchConverter.class);
    Configurations mockConfigurations = Mockito.mock(Configurations.class);
    Mockito.when(mockConfigurations.getCaseLawsIndexName()).thenReturn("caselaws");
    Mockito.when(mockConfigurations.getNormsIndexName()).thenReturn("norms");

    PageUtils instance = new PageUtils(mockConfigurations);
    instance.convertSearchHit(mockSearchHit, mockConverter);
    Mockito.verify(mockConverter).read(Mockito.eq(Norm.class), Mockito.any());
  }
}
