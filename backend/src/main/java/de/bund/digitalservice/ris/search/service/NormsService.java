package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class NormsService {

  private final NormsRepository normsRepository;
  private final ElasticsearchOperations operations;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;
  private final ArticleService articleService;
  private final NormsBucket normsBucket;

  /**
   * Constructs a new instance of {@code NormsService}.
   *
   * @param normsRepository The repository for interacting with the OpenSearch norms.
   * @param normsBucket The object storage bucket for norms files.
   * @param operations The Elasticsearch operations for executing queries.
   * @param simpleSearchQueryBuilder The query builder for constructing search queries.
   */
  @Autowired
  public NormsService(
      NormsRepository normsRepository,
      NormsBucket normsBucket,
      ElasticsearchOperations operations,
      RestHighLevelClient openSearchRestClient,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder,
      ArticleService articleService,
      @Value("${opensearch.norms-index-name}") String normsIndexName) {
    this.normsRepository = normsRepository;
    this.normsBucket = normsBucket;
    this.operations = operations;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
    this.articleService = articleService;
  }

  /**
   * Search and filter norm documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param normsSearchParams Norms search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link Norm}.
   */
  public SearchPage<Norm> simpleSearchNorms(
      @NonNull UniversalSearchParams params,
      @Nullable NormsSearchParams normsSearchParams,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(new NormSimpleSearchType(normsSearchParams)), params, pageable);
    SearchHits<Norm> searchHits = operations.search(query, Norm.class);
    populateNormSearchHitsWithArticleTextMatches(searchHits, params.getSearchTerm(), false);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Returns a {@link Norm} by its expression-level ELI.
   *
   * @param expressionEli the expression-level ELI of the Norm to return
   * @return an {@link Optional} containing the Norm if found, or empty if not found
   */
  public Optional<Norm> getByExpressionEli(final ExpressionEli expressionEli) {
    Norm result = normsRepository.getByExpressionEliKeyword(expressionEli.toString());
    if (result != null) {
      result.setArticles(articleService.findAllByExpressionEli(result.getExpressionEli()));
    }
    return Optional.ofNullable(result);
  }

  /**
   * Retrieves A Norm Ldml file by its ManifestationEli
   *
   * @param eli manifestationEli of a norm
   * @return byte array fileContent
   * @throws ObjectStoreServiceException when a non recoverable objectsStore service exception
   *     occurred
   */
  public Optional<byte[]> getNormFileByEli(ManifestationEli eli)
      throws ObjectStoreServiceException {
    return normsBucket.get(eli.toString());
  }

  /**
   * Retrieves all work_example expression metadata for a given work Eli paginated. The method uses
   * the {@link org.opensearch.client.RestHighLevelClient} to retrieve the docValue fields. An
   * {@link ElasticsearchOperations} search Query will still query the _source of a document,
   * potentially leading to performance issues for big documents
   *
   * @param workEli workEli to retrieve all expression metadata from
   * @param pageable pagination
   * @return Paginated Norms containing work_example metadata
   */
  public Page<Norm> getWorkExpressions(WorkEli workEli, Pageable pageable) {
    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "entryIntoForceDate"));

    return normsRepository.getByWorkEliKeyword(workEli.toString(), sortedPageable);
  }

  /**
   * Writes a zip archive containing all files given
   *
   * @param keys List of bucket keys to archive
   * @param outputStream Outputstream of the zip archive
   * @throws IOException when a given File was not able to be archived
   */
  public void writeZipArchive(List<String> keys, OutputStream outputStream) throws IOException {
    ZipManager.writeZipArchive(normsBucket, keys, outputStream);
  }

  /**
   * @param prefix the prefix for a given norm
   * @return a list of the filenames that match the provided prefix
   */
  public List<String> getAllFilenamesByPath(String prefix) {
    return normsBucket.getAllKeysByPrefix(prefix);
  }

  /**
   * Takes SearchHits of AbstractSearchEntity and populates norm searchHits with the top 3
   * corresponding article hits. It accepts the SearchHits to be from AbstractSearchEntity to enable
   * the allDocuments searches to use the same method. Non Norm SearchHits are ignored.
   *
   * @param searchHits searchHits of any AbstractSearchEntity
   * @param searchString the searchTerm or query used to collect article hits
   * @param isLuceneQuery determine if the searchString is a lucene query or a term
   * @param <T> AbstractSearchEntity
   */
  public <T extends AbstractSearchEntity> void populateNormSearchHitsWithArticleTextMatches(
      SearchHits<T> searchHits, String searchString, boolean isLuceneQuery) {
    if (StringUtils.isEmpty(searchString)) {
      return;
    }

    Map<String, Map<String, SearchHits<?>>> normInnerHitsMap =
        searchHits.stream()
            .filter(hit -> hit.getContent() instanceof Norm)
            .collect(Collectors.toMap(SearchHit::getId, SearchHit::getInnerHits));
    Set<String> expressionElis = normInnerHitsMap.keySet();

    if (expressionElis.isEmpty()) {
      return;
    }

    SearchHits<Article> articles =
        articleService.searchTopThreeArticlesByExpressionELi(
            expressionElis, searchString, isLuceneQuery);

    for (SearchHit<Article> articleSearchHit : articles.getSearchHits()) {
      String expressionEli = articleSearchHit.getContent().getExpressionEli();
      normInnerHitsMap.get(expressionEli).putAll(articleSearchHit.getInnerHits());
    }
  }
}
