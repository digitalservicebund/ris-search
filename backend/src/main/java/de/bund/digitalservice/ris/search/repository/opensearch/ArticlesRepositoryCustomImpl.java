package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.collapse.CollapseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

public class ArticlesRepositoryCustomImpl implements ArticlesRepositoryCustom {

  private final ElasticsearchOperations operations;

  public ArticlesRepositoryCustomImpl(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  @Override
  public Page<Article> findAllByDocumentNumberStartingWithAndDocumentType(
      String documentNumber, LegislationPartType type, Pageable pageable) {

    NativeSearchQuery query =
        new NativeSearchQueryBuilder()
            .withQuery(
                QueryBuilders.boolQuery()
                    .filter(
                        QueryBuilders.prefixQuery(Article.Fields.DOCUMENT_NUMBER, documentNumber))
                    .filter(QueryBuilders.termQuery(Article.Fields.DOCUMENT_TYPE, type.name())))
            .withCollapseBuilder(new CollapseBuilder(Article.Fields.DOCUMENT_NUMBER))
            .withPageable(pageable)
            .build();

    SearchHits<Article> hits = operations.search(query, Article.class);
    SearchPage<Article> searchPage = PageUtils.unwrapSearchHits(hits, pageable);
    return searchPage.map(SearchHit::getContent);
  }
}
