package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
  private final RestHighLevelClient openSearchRestClient;
  private final NormsBucket normsBucket;
  private final String normsIndexName;
  private static final String DATE_FORMAT = "yyyy-MM-dd";

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
      @Value("${opensearch.norms-index-name}") String normsIndexName) {
    this.normsRepository = normsRepository;
    this.normsBucket = normsBucket;
    this.operations = operations;
    this.openSearchRestClient = openSearchRestClient;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
    this.normsIndexName = normsIndexName;
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
      @NotNull UniversalSearchParams params,
      @Nullable NormsSearchParams normsSearchParams,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(new NormSimpleSearchType(normsSearchParams)), params, pageable);
    SearchHits<Norm> searchHits = operations.search(query, Norm.class);

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
    return Optional.ofNullable(result);
  }

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
    SearchSourceBuilder sourceBuilder =
        new SearchSourceBuilder()
            .query(QueryBuilders.termQuery(Norm.Fields.WORK_ELI_KEYWORD, workEli.toString()))
            .sort(Norm.Fields.ENTRY_INTO_FORCE_DATE, SortOrder.DESC)
            .docValueField(Norm.Fields.EXPRESSION_ELI_KEYWORD)
            .docValueField(Norm.Fields.ENTRY_INTO_FORCE_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.EXPIRY_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_TITLE_KEYWORD)
            .docValueField(Norm.Fields.DATE_PUBLISHED, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_SHORT_TITLE_KEYWORD)
            .docValueField(Norm.Fields.NORMS_DATE, DATE_FORMAT)
            .docValueField(Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD)
            .fetchSource(false)
            .from(pageable.getPageNumber() * pageable.getPageSize())
            .size(pageable.getPageSize());

    SearchRequest searchRequest = new SearchRequest(normsIndexName).source(sourceBuilder);

    try {
      SearchResponse response = openSearchRestClient.search(searchRequest, RequestOptions.DEFAULT);
      var hits = response.getHits();

      var norms =
          Arrays.stream(response.getHits().getHits())
              .map(
                  hit -> {
                    var fields = hit.getFields();

                    String expressionEli =
                        getField(fields, Norm.Fields.EXPRESSION_ELI_KEYWORD).orElse(null);
                    hit.getFields().get(Norm.Fields.EXPRESSION_ELI_KEYWORD).getValue();
                    LocalDate entryIntoForceDate =
                        getField(fields, Norm.Fields.ENTRY_INTO_FORCE_DATE)
                            .map(LocalDate::parse)
                            .orElse(null);
                    LocalDate expiryDate =
                        getField(fields, Norm.Fields.EXPIRY_DATE)
                            .map(LocalDate::parse)
                            .orElse(null);
                    LocalDate datePublished =
                        getField(fields, Norm.Fields.DATE_PUBLISHED)
                            .map(LocalDate::parse)
                            .orElse(null);
                    LocalDate normsDate =
                        getField(fields, Norm.Fields.NORMS_DATE).map(LocalDate::parse).orElse(null);

                    String officialTitle =
                        getField(fields, Norm.Fields.OFFICIAL_TITLE_KEYWORD).orElse(null);
                    String shortTitle =
                        getField(fields, Norm.Fields.OFFICIAL_SHORT_TITLE_KEYWORD).orElse(null);
                    String abbreviation =
                        getField(fields, Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD).orElse(null);
                    return Norm.builder()
                        .id(hit.getId())
                        .officialAbbreviation(abbreviation)
                        .officialShortTitle(shortTitle)
                        .officialTitle(officialTitle)
                        .datePublished(datePublished)
                        .expressionEli(expressionEli)
                        .normsDate(normsDate)
                        .entryIntoForceDate(entryIntoForceDate)
                        .expiryDate(expiryDate)
                        .build();
                  })
              .toList();

      long totalHits = Objects.isNull(hits.getTotalHits()) ? 0 : hits.getTotalHits().value();
      return new PageImpl<>(norms, pageable, totalHits);
    } catch (IOException e) {
      throw new OpenSearchException(e);
    }
  }

  private Optional<String> getField(Map<String, DocumentField> fields, String field) {
    if (fields.get(field) != null) {
      return Optional.of(fields.get(field).getValue());
    }
    return Optional.empty();
  }

  public void writeZipArchive(List<String> keys, OutputStream outputStream) throws IOException {
    ZipManager.writeZipArchive(normsBucket, keys, outputStream);
  }

  public List<String> getAllFilenamesByPath(String prefix) {
    return normsBucket.getAllKeysByPrefix(prefix);
  }
}
