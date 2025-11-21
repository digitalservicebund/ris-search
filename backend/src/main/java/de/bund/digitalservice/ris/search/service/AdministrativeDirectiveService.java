package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.api.parameters.AdministrativeDirectiveSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.util.List;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link org.springframework.stereotype.Service} to indicate that it's a service
 * component in the Spring context.
 */
@Service
public class AdministrativeDirectiveService {
  private final AdministrativeDirectiveRepository repository;
  private final AdministrativeDirectiveBucket bucket;
  private final ElasticsearchOperations operations;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;

  @SneakyThrows
  @Autowired
  public AdministrativeDirectiveService(
      AdministrativeDirectiveRepository repository,
      AdministrativeDirectiveBucket bucket,
      ElasticsearchOperations operations,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder) {
    this.repository = repository;
    this.bucket = bucket;
    this.operations = operations;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
  }

  /**
   * Search and filter administrative directives.
   *
   * @param params Search parameters
   * @param searchParams AdministrativeDirective search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link org.springframework.data.elasticsearch.core.SearchPage} of the containing
   *     {@link de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective}.
   */
  public SearchPage<AdministrativeDirective> simpleSearch(
      @NotNull UniversalSearchParams params,
      @Nullable AdministrativeDirectiveSearchParams searchParams,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(new AdministrativeDirectiveSimpleSearchType(searchParams)), params, pageable);
    SearchHits<AdministrativeDirective> searchHits =
        operations.search(query, AdministrativeDirective.class);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }
}
