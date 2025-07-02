package de.bund.digitalservice.ris.search.nlex.mapper;

import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
import de.bund.digitalservice.ris.search.nlex.schema.result.Error;
import de.bund.digitalservice.ris.search.nlex.schema.result.ExternUrl;
import de.bund.digitalservice.ris.search.nlex.schema.result.Navigation;
import de.bund.digitalservice.ris.search.nlex.schema.result.Page;
import de.bund.digitalservice.ris.search.nlex.schema.result.Para;
import de.bund.digitalservice.ris.search.nlex.schema.result.ParagraphRoles;
import de.bund.digitalservice.ris.search.nlex.schema.result.References;
import de.bund.digitalservice.ris.search.nlex.schema.result.RequestResult;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultList;
import de.bund.digitalservice.ris.search.nlex.schema.result.ResultStatus;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import java.util.List;
import org.springframework.data.elasticsearch.core.SearchPage;

public abstract class RisToNlexMapper {
  private RisToNlexMapper() {}

  public static RequestResult buildNlexStandardErrorRequestResult(String cause) {
    return new RequestResult()
        .setStatus(ResultStatus.ERROR)
        .setErrors(List.of(new Error().setCause(cause)));
  }

  public static RequestResult normsToNlexRequestResult(String id, SearchPage<Norm> searchPage) {

    List<Document> documents =
        searchPage.getSearchHits().stream()
            .map(
                hit -> {
                  Norm norm = hit.getContent();
                  List<TextMatchSchema> matches = NormSearchResponseMapper.getTextMatches(hit);
                  List<Para> zoomParagraphs =
                      matches.stream()
                          .map(
                              match ->
                                  new Para().setValue(match.text()).setRoles(ParagraphRoles.ZOOM))
                          .toList();

                  Document doc =
                      new Document()
                          .setReferences(
                              new References()
                                  .setExternUrl(new ExternUrl().setHref(norm.getHtmlContentUrl())));
                  doc.setContent(
                      new Content()
                          .setTitle(norm.getOfficialTitle())
                          .setLang(Content.LANG_DE_DE)
                          .setParaList(zoomParagraphs));

                  return doc;
                })
            .toList();

    RequestResult result = new RequestResult().setStatus(ResultStatus.OK);
    ResultList rl = new ResultList();
    rl.setDocuments(documents);
    rl.setNavigation(
        new Navigation().setRequestId(id).setPage(new Page().setNumber(searchPage.getNumber())));

    result.setResultList(rl);

    return result;
  }
}
