package de.bund.digitalservice.ris.search.nlex.service;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.mapper.RisToNlexMapper;
import de.bund.digitalservice.ris.search.nlex.schema.query.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.service.NormsService;
import java.util.Objects;
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
    String searchTerm = this.getSearchTerm(query);

    return this.runQuery(
        searchTerm, PageRequest.of(query.getNavigation().getPage().getNumber(), 20));
  }

  private RequestResult runQuery(String searchTerm, Pageable pageable) {
    UniversalSearchParams searchParams = new UniversalSearchParams();
    searchParams.setSearchTerm(searchTerm);
    SearchPage<Norm> normPage =
        this.normsService.searchAndFilterNorms(searchParams, null, pageable);

    String requestId = Base64.encode(searchTerm.getBytes());
    return RisToNlexMapper.normsToNlexRequestResult(requestId, normPage);
  }

  private String getSearchTerm(Query query) {
    Navigation navigation = query.getNavigation();
    if (Objects.isNull(navigation.getRequestId()) || navigation.getRequestId().isEmpty()) {
      return query.getCriteria().getWords().getContains();
    } else {
      return new String(Base64.decode(navigation.getRequestId()));
    }
  }
}
