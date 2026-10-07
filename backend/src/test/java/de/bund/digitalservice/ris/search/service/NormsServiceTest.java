package de.bund.digitalservice.ris.search.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@ExtendWith(MockitoExtension.class)
class NormsServiceTest {

  private static final String DOCUMENT_NUMBER = "DKNR0E80B0026DKNE000100010";
  private static final String ELI_1 = "eli/bund/bgbl-1/2020/s1126/2025-05-05/1/deu";
  private static final String ELI_2 = "eli/bund/bgbl-1/2020/s1126/2026-05-05/1/deu";

  @Mock NormsRepository normsRepository;

  @Mock NormsBucket normsBucket;

  @Mock ElasticsearchOperations operations;

  @Mock SimpleSearchQueryBuilder simpleSearchQueryBuilder;

  @Mock ArticleService articleService;

  NormsService service;

  @BeforeEach
  void setup() {
    service =
        new NormsService(
            normsRepository,
            normsBucket,
            operations,
            simpleSearchQueryBuilder,
            articleService,
            "norms");
  }

  @Test
  void getAllNormsContainingArticleQueriesNormsByTheExpressionElisOfTheArticle() {
    when(articleService.findExpressionElisByDocumentNumber(DOCUMENT_NUMBER))
        .thenReturn(List.of(ELI_1, ELI_2));
    Page<Norm> expected =
        new PageImpl<>(
            List.of(
                Norm.builder().id(ELI_1).expressionEli(ELI_1).build(),
                Norm.builder().id(ELI_2).expressionEli(ELI_2).build()));
    when(normsRepository.findAllByIdIn(
            List.of(ELI_1, ELI_2),
            Pageable.unpaged(Sort.by(Sort.Direction.DESC, "entryIntoForceDate"))))
        .thenReturn(expected);

    Page<Norm> actual = service.getAllNormsContainingArticle(DOCUMENT_NUMBER);

    assertThat(actual).isSameAs(expected);
  }

  @Test
  void getAllNormsContainingArticleReturnsEmptyPageWithoutQueryingNormsIfNoArticleMatches() {
    when(articleService.findExpressionElisByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(List.of());

    Page<Norm> actual = service.getAllNormsContainingArticle(DOCUMENT_NUMBER);

    assertThat(actual).isEmpty();
    verifyNoInteractions(normsRepository);
  }
}
