package de.bund.digitalservice.ris.search.nlex.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import java.util.List;

public abstract class RisToNlexMapper {
  private RisToNlexMapper() {}

  public static RequestResult normsToNlexRequestResult(List<Norm> norms) {
    List<Document> documents =
        norms.stream()
            .map(
                norm -> {
                  Document doc = new Document();
                  References refs = new References();
                  ExternUrl url = new ExternUrl();
                  url.setHref(norm.getHtmlContentUrl());
                  refs.setExternUrl(url);
                  doc.setReferences(refs);

                  return doc;
                })
            .toList();

    RequestResult result = new RequestResult();
    ResultList rl = new ResultList();
    rl.setDocuments(documents);
    result.setResultList(rl);
    return result;
  }
}
