package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.mapper.RisToNlexMapper;
import de.bund.digitalservice.ris.search.nlex.schema.query.BooleanAnd;
import de.bund.digitalservice.ris.search.nlex.schema.query.Criteria;
import de.bund.digitalservice.ris.search.nlex.schema.query.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.query.Page;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.query.Words;
import de.bund.digitalservice.ris.search.nlex.schema.result.Error;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jose4j.base64url.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for processing and executing queries related to norms. This class
 * provides functionalities to parse search terms, handle pagination, and return results based on
 * search criteria.
 */
@Service
public class NlexService {

  private final NormsService normsService;

  private final String frontendUrl;

  /**
   * Constructs an instance of the NlexService class, initializing it with the necessary
   * dependencies.
   *
   * @param normsService the service used to perform operations related to norms
   * @param frontendUrl the base URL for the frontend, which is concatenated with the "norms/" path
   */
  public NlexService(
      NormsService normsService, @Value("${server.front-end-url}") String frontendUrl) {
    this.normsService = normsService;
    this.frontendUrl = frontendUrl + "norms/";
  }

  /**
   * Runs a query and returns the result. The searchTerm is either supplied in the words contains
   * object or with the requestId. The requestId is a base64 encoded searchTerm to enable pagination
   * without persisting the preceding query.
   *
   * @param query Query object
   * @return RequestResult
   */
  public RequestResult runRequestQuery(Query query) {
    return getSearchTerm(query)
        .map(
            searchTerm -> {
              var pageNumber =
                  Optional.ofNullable(query.getNavigation().getPage())
                      .map(Page::getNumber)
                      .orElse(0);

              return runQuery(searchTerm, PageRequest.of(pageNumber, 20));
            })
        .orElse(
            new RequestResult()
                .setErrors(List.of(new Error().setCause(Error.STANDARD_ERROR_NO_SEARCHTERM))));
  }

  /**
   * @param searchTerm searchTerm to query for
   * @param pageable pagination parameters
   * @return RequestResult
   */
  private RequestResult runQuery(String searchTerm, Pageable pageable) {
    UniversalSearchParams searchParams = new UniversalSearchParams();
    searchParams.setSearchTerm(searchTerm);
    SearchPage<Norm> normPage = normsService.simpleSearchNorms(searchParams, null, pageable);

    String requestId = Base64.encode(searchTerm.getBytes());
    return RisToNlexMapper.normsToNlexRequestResult(requestId, frontendUrl, normPage);
  }

  /**
   * it parses the searchterm either from the base64 encoded id or the given contains tag
   *
   * @param query Query object
   * @return a given searchTerm if not empty
   */
  private Optional<String> getSearchTerm(Query query) {
    Navigation navigation = query.getNavigation();

    if (Objects.isNull(navigation.getRequestId()) || navigation.getRequestId().isEmpty()) {
      // not sure how empty searches are represented in the schema. Safeguarding against
      // null tags and empty strings
      var contains =
          Optional.ofNullable(query.getCriteria())
              .map(Criteria::getWords)
              .map(Words::getContains)
              .orElseGet(
                  () ->
                      Optional.ofNullable(query.getCriteria())
                          .map(Criteria::getAnd)
                          .map(BooleanAnd::getWords)
                          .map(Words::getContains)
                          .orElse(""));

      if (contains.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(contains);

    } else {
      return Optional.of(new String(Base64.decode(navigation.getRequestId())));
    }
  }
}
