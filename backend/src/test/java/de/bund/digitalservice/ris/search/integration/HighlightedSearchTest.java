package de.bund.digitalservice.ris.search.integration;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.schema.TextMatchSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class HighlightedSearchTest extends ContainersIntegrationBase {

  String allDocUrl = ApiConfig.Paths.DOCUMENT + "?searchTerm=%s";
  String caseLawUrl = ApiConfig.Paths.CASELAW + "?searchTerm=%s";

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Case law highlight test")
  void caseLawHighlightTest() {
    var example = CaseLawTestData.highlightExample;

    checkTopHighlightForCaseLawSearchesIsCorrect("caseFacts", example.caseFacts());
    checkTopHighlightForCaseLawSearchesIsCorrect("decisionGrounds", example.decisionGrounds());
    checkTopHighlightForCaseLawSearchesIsCorrect("decisionName", example.decisionName().getFirst());
    checkTopHighlightForCaseLawSearchesIsCorrect("dissentingOpinion", example.dissentingOpinion());
    checkTopHighlightForCaseLawSearchesIsCorrect("ecli", example.ecli());
    checkTopHighlightForCaseLawSearchesIsCorrect(
        "erledigungsvermerk", example.erledigungsvermerk());
    checkTopHighlightForCaseLawSearchesIsCorrect("fileNumbers", example.fileNumbers().getFirst());
    checkTopHighlightForCaseLawSearchesIsCorrect("grounds", example.grounds());
    checkTopHighlightForCaseLawSearchesIsCorrect("guidingPrinciple", example.guidingPrinciple());
    checkTopHighlightForCaseLawSearchesIsCorrect("headline", example.headline());
    checkTopHighlightForCaseLawSearchesIsCorrect("headnote", example.headnote());
    checkTopHighlightForCaseLawSearchesIsCorrect("otherHeadnote", example.otherHeadnote());
    checkTopHighlightForCaseLawSearchesIsCorrect("otherLongText", example.otherLongText());
    checkTopHighlightForCaseLawSearchesIsCorrect("outline", example.outline());
    checkTopHighlightForCaseLawSearchesIsCorrect("rechtsfrage", example.rechtsfrage());
    checkTopHighlightForCaseLawSearchesIsCorrect("rechtsfrageGesamt", example.rechtsfrageGesamt());
    checkTopHighlightForCaseLawSearchesIsCorrect("tenor", example.tenor());
    checkTopHighlightForCaseLawSearchesIsCorrect("titleLine", example.titleLine());
  }

  private void checkTopHighlightForCaseLawSearchesIsCorrect(
      String highlightName, String searchString) {
    checkTopHighlightOnUrlIs(allDocUrl, highlightName, searchString);
    checkTopHighlightOnUrlIs(caseLawUrl, highlightName, searchString);
  }

  private void checkTopHighlightOnUrlIs(String url, String highlightName, String searchString) {
    TextMatchSchema topAllDocTextMatch =
        getTopHitTextMatches(mockMvc, String.format(url, searchString)).getFirst();
    assertThat(topAllDocTextMatch)
        .isEqualTo(
            TextMatchSchema.builder()
                .name(highlightName)
                .text("<mark>" + searchString + "</mark>")
                .build());
  }
}
