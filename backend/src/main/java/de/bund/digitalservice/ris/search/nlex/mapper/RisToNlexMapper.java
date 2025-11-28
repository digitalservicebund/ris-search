package de.bund.digitalservice.ris.search.nlex.mapper;

import de.bund.digitalservice.ris.search.mapper.NormSearchResponseMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.nlex.schema.result.Content;
import de.bund.digitalservice.ris.search.nlex.schema.result.Document;
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

/**
 * The {@code RisToNlexMapper} class provides a utility method to map search results related to
 * legal norms into a structured request result format compliant with application-specific schemas.
 *
 * <p>This class operates on search results of type {@link SearchPage} containing instances of
 * {@link Norm} and produces a {@link RequestResult} containing formatted document information,
 * navigation details, and metadata based on provided inputs.
 *
 * <p>Functionality: - It processes the search results, extracting hit data to create documents. -
 * The documents include references, content with titles, zoom paragraphs with roles, and external
 * URLs mapped to the provided frontend URL. - It formulates a navigation object with request ID,
 * total hit count, and pagination details, which are included in the resulting {@link
 * RequestResult}.
 *
 * <p>Key Components: - {@link RequestResult}: Class representing the result of the mapping,
 * encapsulating documents, navigation, and status information. - {@link SearchPage}
 */
public abstract class RisToNlexMapper {
  private RisToNlexMapper() {}

  /**
   * Transforms the given search results for norms into a {@link RequestResult} object, mapped
   * according to the application's search schema.
   *
   * @param requestId the unique identifier for the request, used for navigation purposes
   * @param frontendUrl the base URL for constructing external links to norms
   * @param searchPage the paginated search results containing norms and associated metadata
   * @return a {@link RequestResult} containing the mapped documents and navigation data
   */
  public static RequestResult normsToNlexRequestResult(
      String requestId, String frontendUrl, SearchPage<Norm> searchPage) {

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
                                  .setExternUrl(
                                      new ExternUrl()
                                          .setHref(frontendUrl + norm.getExpressionEli())));
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
        new Navigation()
            .setRequestId(requestId)
            .setHits(searchPage.getTotalElements())
            .setPage(
                new Page()
                    .setSize(searchPage.getNumberOfElements())
                    .setNumber(searchPage.getNumber())));

    result.setResultList(rl);

    return result;
  }
}
