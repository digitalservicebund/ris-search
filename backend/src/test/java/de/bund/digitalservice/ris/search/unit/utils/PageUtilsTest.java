package de.bund.digitalservice.ris.search.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;

class PageUtilsTest {

  @Test
  void testConvertSnakeCaseKeysToCamelCase() {
    var expectations =
        Map.of("UPPERCASE", "UPPERCASE", "camelCase", "camelCase", "snake_case", "snakeCase");

    for (var entry : expectations.entrySet()) {
      assertThat(PageUtils.snakeCaseToCamelCase(entry.getKey())).isEqualTo(entry.getValue());
    }
  }

  @Test
  void testConvertSearchHit_IngoresUnexpectedIndex() {
    @SuppressWarnings("unchecked")
    var mockSearchHit = (SearchHit<Document>) Mockito.mock(SearchHit.class);
    ElasticsearchConverter mockConverter = Mockito.mock(ElasticsearchConverter.class);
    Configurations mockConfigurations = Mockito.mock(Configurations.class);
    Mockito.when(mockConfigurations.getCaseLawsIndexName()).thenReturn("caselaws");
    Mockito.when(mockConfigurations.getLiteratureIndexName()).thenReturn("literature");
    Mockito.when(mockConfigurations.getNormsIndexName()).thenReturn("norms");
    Mockito.when(mockConfigurations.getAdministrativeDirectiveIndexName())
        .thenReturn("administrative_directive");
    Mockito.when(mockSearchHit.getIndex()).thenReturn("unexpectedIndex");

    PageUtils instance = new PageUtils(mockConfigurations);

    Optional<SearchHit<AbstractSearchEntity>> searchHit =
        instance.convertSearchHit(mockSearchHit, mockConverter);

    assertThat(searchHit).isEmpty();
  }

  @Test
  void testConvertSearchHit_ConvertsDocumentsFromSubIndices() {
    var mockSearchHit = Mockito.mock(SearchHit.class);
    Mockito.when(mockSearchHit.getIndex()).thenReturn("norms_2025");

    ElasticsearchConverter mockConverter = Mockito.mock(ElasticsearchConverter.class);
    Configurations mockConfigurations = Mockito.mock(Configurations.class);
    Mockito.when(mockConfigurations.getCaseLawsIndexName()).thenReturn("caselaws");
    Mockito.when(mockConfigurations.getLiteratureIndexName()).thenReturn("literature");
    Mockito.when(mockConfigurations.getNormsIndexName()).thenReturn("norms");

    PageUtils instance = new PageUtils(mockConfigurations);
    instance.convertSearchHit(mockSearchHit, mockConverter);
    Mockito.verify(mockConverter).read(Mockito.eq(Norm.class), Mockito.any());
  }
}
