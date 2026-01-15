package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.CourtSearchResult;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.helper.CourtNameAbbreviationExpander;
import de.bund.digitalservice.ris.search.service.helper.ZipManager;
import de.bund.digitalservice.ris.search.utils.PageUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchAggregations;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the database and return the search results. This class is
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
@Profile({"default", "staging", "uat", "test", "prototype"})
public class CaseLawService {
  private final CaseLawRepository caseLawRepository;
  private final CaseLawBucket caseLawBucket;
  private final ElasticsearchOperations operations;
  private final CourtNameAbbreviationExpander courtNameAbbreviationExpander;
  private final Configurations configurations;
  private final CaseLawLdmlToOpenSearchMapper marshaller;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;

  /**
   * Constructs a new instance of the CaseLawService class, initializing its dependencies.
   *
   * @param caseLawRepository the repository responsible for managing CaseLaw entities
   * @param caseLawBucket the bucket for storing and managing case law data in bulk operations
   * @param operations the ElasticsearchOperations instance to interact with Elasticsearch
   * @param configurations the configurations required for the service
   * @param marshaller the mapper for converting CaseLaw entities to OpenSearch format
   * @param simpleSearchQueryBuilder the builder used for constructing simple search queries
   */
  @SneakyThrows
  @Autowired
  public CaseLawService(
      CaseLawRepository caseLawRepository,
      CaseLawBucket caseLawBucket,
      ElasticsearchOperations operations,
      Configurations configurations,
      CaseLawLdmlToOpenSearchMapper marshaller,
      SimpleSearchQueryBuilder simpleSearchQueryBuilder) {
    this.caseLawRepository = caseLawRepository;
    this.caseLawBucket = caseLawBucket;
    this.operations = operations;
    this.configurations = configurations;
    this.courtNameAbbreviationExpander = new CourtNameAbbreviationExpander();
    this.marshaller = marshaller;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
  }

  /**
   * Search and filter case law documents. The query syntax is documented in the <a
   * href="https://neuris-portal-api-docs-production.obs-website.eu-de.otc.t-systems.com/guides/filters/#date-filters">API
   * docs</a>.
   *
   * @param params Search parameters
   * @param caseLawParams Case law search parameters
   * @param pageable Page (offset) and size parameters.
   * @return A new {@link SearchPage} of the containing {@link CaseLawDocumentationUnit}.
   */
  public SearchPage<CaseLawDocumentationUnit> simpleSearchCaseLaw(
      @NotNull UniversalSearchParams params,
      @Nullable CaseLawSearchParams caseLawParams,
      Pageable pageable) {

    NativeSearchQuery query =
        simpleSearchQueryBuilder.buildQuery(
            List.of(new CaseLawSimpleSearchType(caseLawParams)), params, pageable);
    SearchHits<CaseLawDocumentationUnit> searchHits =
        operations.search(query, CaseLawDocumentationUnit.class);

    return PageUtils.unwrapSearchHits(searchHits, pageable);
  }

  /**
   * Retrieves a list of court search results based on the specified search prefix.
   *
   * @param searchPrefix the prefix used to filter and search for court names; can be null for no
   *     filtering
   * @return a list of {@code CourtSearchResult} objects containing court-related key, document
   *     count, and expanded label
   */
  public List<CourtSearchResult> getCourts(String searchPrefix) {

    var filterQuery =
        searchPrefix == null
            ? null
            : new MatchPhrasePrefixQueryBuilder("court_keyword", searchPrefix.toLowerCase());
    final String aggregationName = "t";
    var termsAggregation =
        new TermsAggregationBuilder(aggregationName).field("court_keyword.keyword");

    var mainQuery =
        new NativeSearchQueryBuilder()
            .withQuery(filterQuery)
            .withAggregations(termsAggregation)
            .withMaxResults(
                0); // we only care about the aggregation buckets, not the underlying query data

    SearchHits<Void> searchHits =
        operations.search(
            mainQuery.build(),
            Void.class,
            IndexCoordinates.of(configurations.getCaseLawsIndexName()));

    var buckets = getBuckets(searchHits, aggregationName);
    var firstToken = CourtNameAbbreviationExpander.extractFirstToken(searchPrefix);
    return buckets.stream()
        .map(
            item -> {
              String key = item.getKeyAsString();
              long count = item.getDocCount();
              String label =
                  courtNameAbbreviationExpander.getLabelExpandingSynonyms(key, firstToken);
              return new CourtSearchResult(key, count, label);
            })
        .toList();
  }

  private static List<? extends Terms.Bucket> getBuckets(
      SearchHits<Void> searchHits, String aggregationName) {
    OpenSearchAggregations aggregationsWrapper =
        (OpenSearchAggregations) searchHits.getAggregations();
    assert aggregationsWrapper != null;
    Aggregations aggregations = aggregationsWrapper.aggregations();
    ParsedStringTerms counts = (ParsedStringTerms) aggregations.getAsMap().get(aggregationName);
    assert counts != null;
    return counts.getBuckets();
  }

  public List<CaseLawDocumentationUnit> getByDocumentNumber(String documentNumber) {
    return caseLawRepository.findByDocumentNumber(documentNumber);
  }

  public Optional<byte[]> getFileByDocumentNumber(String documentNumber)
      throws ObjectStoreServiceException {
    return caseLawBucket.get(String.format("%s/%s.xml", documentNumber, documentNumber));
  }

  public Optional<byte[]> getFileByPath(String path) throws ObjectStoreServiceException {
    return caseLawBucket.get(path);
  }

  public void writeZipArchive(List<String> keys, OutputStream outputStream) throws IOException {
    ZipManager.writeZipArchive(caseLawBucket, keys, outputStream);
  }

  public List<String> getAllFilenamesByDocumentNumber(String documentNumber) {
    return caseLawBucket.getAllKeysByPrefix(documentNumber);
  }

  /**
   * Retrieves a `CaseLawDocumentationUnit` object from the bucket using the provided filename. If
   * the file content is not found or an error occurs during processing, an empty Optional is
   * returned.
   *
   * @param filename the name of the file to retrieve from the bucket
   * @return an Optional containing the `CaseLawDocumentationUnit` if successfully retrieved and
   *     parsed, or an empty Optional if not found or an error occurs
   * @throws ObjectStoreServiceException if an error occurs while accessing the object storage
   *     service
   */
  public Optional<CaseLawDocumentationUnit> getFromBucket(String filename)
      throws ObjectStoreServiceException {
    Optional<String> contentOption = caseLawBucket.getFileAsString(filename);

    if (contentOption.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(marshaller.fromString(contentOption.get()));
    } catch (OpenSearchMapperException ex) {
      return Optional.empty();
    }
  }
}
