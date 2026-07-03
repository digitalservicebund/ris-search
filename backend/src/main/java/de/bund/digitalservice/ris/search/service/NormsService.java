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
import de.bund.digitalservice.ris.search.utils.DateUtils;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import de.bund.digitalservice.ris.search.utils.eli.ManifestationEli;
import de.bund.digitalservice.ris.search.utils.eli.WorkEli;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.opensearch.OpenSearchException;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.document.DocumentField;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  private final RestHighLevelClient openSearchRestClient;
  private final NormsBucket normsBucket;
  private final String versionPrefix;
  private final String normsIndexName;
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  /**
   * Constructs a new instance of {@code NormsService}.
   *
   * @param normsRepository The repository for interacting with the OpenSearch norms.
   * @param normsBucket The object storage bucket for norms files.
   * @param versionPrefix the version prefix for the bucket
   * @param operations The Elasticsearch operations for executing queries.
   * @param simpleSearchQueryBuilder The query builder for constructing search queries.
   */
  @Autowired
  public NormsService(
      NormsRepository normsRepository,
      NormsBucket normsBucket,
      @Value("${s3.file-storage.norm.versionPrefix}") String versionPrefix,
      ElasticsearchOperations operations,
      RestHighLevelClient openSearchRestClient,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder,
      ArticleService articleService,
      @Value("${opensearch.norms-index-name}") String normsIndexName) {
    this.normsRepository = normsRepository;
    this.normsBucket = normsBucket;
    this.versionPrefix = versionPrefix;
    this.operations = operations;
    this.openSearchRestClient = openSearchRestClient;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
    this.normsIndexName = normsIndexName;
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
    Norm result = normsRepository.getByExpressionEli(expressionEli.toString());
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
    return normsBucket.get(versionPrefix + eli.toString());
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
    SearchSourceBuilder sourceBuilder =
        new SearchSourceBuilder()
            .query(QueryBuilders.termQuery(Norm.Fields.WORK_ELI_KEYWORD, workEli.toString()))
            .sort(Norm.Fields.ENTRY_INTO_FORCE_DATE, SortOrder.DESC)
            .docValueField(Norm.Fields.EXPRESSION_ELI_KEYWORD)
            .docValueField(Norm.Fields.WORK_ELI_KEYWORD)
            .docValueField(Norm.Fields.ENTRY_INTO_FORCE_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.EXPIRY_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_TITLE_KEYWORD)
            .docValueField(Norm.Fields.DATE_PUBLISHED, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_SHORT_TITLE_KEYWORD)
            .docValueField(Norm.Fields.NORMS_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD)
            .docValueField(Norm.Fields.LATEST_MANIFESTATION_ELI_KEYWORD)
            .fetchSource(false)
            .from(pageable.getPageNumber() * pageable.getPageSize())
            .size(pageable.getPageSize());

    SearchRequest searchRequest = new SearchRequest(normsIndexName).source(sourceBuilder);

    try {
      SearchResponse response = openSearchRestClient.search(searchRequest, RequestOptions.DEFAULT);
      var hits = response.getHits();
      var norms =
          Arrays.stream(hits.getHits())
              .map(
                  hit -> {
                    var fields = hit.getFields();

                    String returnedWorkEli = getField(fields, Norm.Fields.WORK_ELI_KEYWORD);
                    String expressionEli = getField(fields, Norm.Fields.EXPRESSION_ELI_KEYWORD);
                    LocalDate entryIntoForceDate =
                        DateUtils.nullSafeParseyyyyMMdd(
                            getField(fields, Norm.Fields.ENTRY_INTO_FORCE_DATE));
                    LocalDate expiryDate =
                        DateUtils.nullSafeParseyyyyMMdd(getField(fields, Norm.Fields.EXPIRY_DATE));
                    LocalDate datePublished =
                        DateUtils.nullSafeParseyyyyMMdd(
                            getField(fields, Norm.Fields.DATE_PUBLISHED));
                    LocalDate normsDate =
                        DateUtils.nullSafeParseyyyyMMdd(getField(fields, Norm.Fields.NORMS_DATE));

                    String officialTitle = getField(fields, Norm.Fields.OFFICIAL_TITLE_KEYWORD);
                    String shortTitle = getField(fields, Norm.Fields.OFFICIAL_SHORT_TITLE_KEYWORD);
                    String manifestationEli =
                        getField(fields, Norm.Fields.LATEST_MANIFESTATION_ELI_KEYWORD);
                    String abbreviation =
                        getField(fields, Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD);
                    return Norm.builder()
                        .id(hit.getId())
                        .workEli(returnedWorkEli)
                        .officialAbbreviation(abbreviation)
                        .officialShortTitle(shortTitle)
                        .officialTitle(officialTitle)
                        .datePublished(datePublished)
                        .expressionEli(expressionEli)
                        .normsDate(normsDate)
                        .entryIntoForceDate(entryIntoForceDate)
                        .expiryDate(expiryDate)
                        .manifestationEliExample(manifestationEli)
                        .build();
                  })
              .toList();

      long totalHits = Objects.isNull(hits.getTotalHits()) ? 0 : hits.getTotalHits().value();
      return new PageImpl<>(norms, pageable, totalHits);
    } catch (IOException e) {
      throw new OpenSearchException(e);
    }
  }

  /**
   * Returns the Optional of the string value of a DocumentField if it is part of the fields Map
   *
   * @param fields Map of Fieldname and DocumentField of a Searchhit
   * @param field the field to retrieve from the Map
   * @return Optional of a the string value of DocumentField
   */
  private @Nullable String getField(Map<String, DocumentField> fields, String field) {
    return fields.get(field) != null ? fields.get(field).getValue() : null;
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
