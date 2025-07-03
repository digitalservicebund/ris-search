package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.mapper.RisToNlexMapper;
import de.bund.digitalservice.ris.search.nlex.schema.query.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.result.Error;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jose4j.base64url.Base64;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
public class NlexService {

  NormsService normsService;

  public NlexService(NormsService normsService) {
    this.normsService = normsService;
  }

  public RequestResult runRequestQuery(Query query) {
    String searchTerm = getSearchTerm(query);
    if (Objects.isNull(searchTerm) || searchTerm.isEmpty()) {
      return new RequestResult()
          .setErrors(List.of(new Error().setCause(Error.STANDARD_ERROR_NO_SEARCHTERM)));
    }

    return runQuery(searchTerm, PageRequest.of(query.getNavigation().getPage().getNumber(), 20));
  }

  private RequestResult runQuery(String searchTerm, Pageable pageable) {
    UniversalSearchParams searchParams = new UniversalSearchParams();
    searchParams.setSearchTerm(searchTerm);
    SearchPage<Norm> normPage = normsService.searchAndFilterNorms(searchParams, null, pageable);

    String requestId = Base64.encode(searchTerm.getBytes());
    return RisToNlexMapper.normsToNlexRequestResult(requestId, normPage);
  }

  private String getSearchTerm(Query query) {
    Navigation navigation = query.getNavigation();
    if (Objects.isNull(navigation.getRequestId()) || navigation.getRequestId().isEmpty()) {
      // if it turns out the words tag isn't wrapped in an and tag we can remove it
      var searchTerm = Optional.ofNullable(query.getCriteria().getWords().getContains());
      return searchTerm.orElseGet(() -> query.getCriteria().getAnd().getWords().getContains());
    } else {
      return new String(Base64.decode(navigation.getRequestId()));
    }
  }
}
