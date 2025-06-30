package de.bund.digitalservice.ris.search.nlex.mapper;

import de.bund.digitalservice.ris.search.models.api.parameters.UniversalSearchParams;
import de.bund.digitalservice.ris.search.nlex.schema.query.Query;
import jakarta.xml.bind.JAXBException;

public abstract class NlexToRisMapper {

  private NlexToRisMapper() {}

  public static UniversalSearchParams mapRequestToSearchParams(Query request) throws JAXBException {

    UniversalSearchParams params = new UniversalSearchParams();
    String searchTerm = request.getCriteria().getWords().getContains();
    params.setSearchTerm(searchTerm);

    return params;
  }
}
