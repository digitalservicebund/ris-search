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
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
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
 * annotated with {@link Service} to indicate that it's a service component in the Spring context.
 */
@Service
public class NormsService {

  private final NormsRepository normsRepository;
  private final ElasticsearchOperations operations;
  private final SimpleSearchQueryBuilder simpleSearchQueryBuilder;
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
      SimpleSearchQueryBuilder simpleSearchQueryBuilder) {
    this.normsRepository = normsRepository;
    this.normsBucket = normsBucket;
    this.operations = operations;
    this.simpleSearchQueryBuilder = simpleSearchQueryBuilder;
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

  public void writeZipArchive(List<String> keys, OutputStream outputStream) throws IOException {
    ZipManager.writeZipArchive(normsBucket, keys, outputStream);
  }

  public List<String> getAllFilenamesByPath(String prefix) {
    return normsBucket.getAllKeysByPrefix(prefix);
  }
}
