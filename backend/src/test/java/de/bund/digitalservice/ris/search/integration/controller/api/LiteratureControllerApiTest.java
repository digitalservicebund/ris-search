package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWithIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class LiteratureControllerApiTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  private final String documentNumberPresentInBucket = "XXLU000000001";

  @Test
  @DisplayName("Should return literature when using api endpoint with document number")
  void shouldReturnSingleLiteratureJson() throws Exception {

    String documentNumberPersistedInTest = "KALU000000002";
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "/" + documentNumberPersistedInTest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.documentNumber", Matchers.is(documentNumberPersistedInTest)),
            jsonPath("$.recordingDate", Matchers.is("1998-01-01")),
            jsonPath("$.yearsOfPublication", Matchers.containsInAnyOrder("1999", "2000", "2001")),
            jsonPath("$.documentTypes", Matchers.containsInAnyOrder("Kommentar", "Aufsatz")),
            jsonPath("$.dependentReferences", Matchers.contains("NJW, 2000, 456-789")),
            jsonPath(
                "$.independentReferences",
                Matchers.contains("Festschrift für Müller, 2001, 12-34")),
            jsonPath("$.normReferences", Matchers.contains("GG, Art 6 Abs 2 S 1")),
            jsonPath("$.headline", Matchers.is("Literatur. Zivilprozessrecht im Wandel")),
            jsonPath("$.alternativeHeadline", Matchers.is("Dokumentation ZPO")),
            jsonPath("$.headlineAdditions", Matchers.is("Zusatz zu Zivilprozessrecht im Wandel")),
            jsonPath("$.authors", Matchers.containsInAnyOrder("Schmidt, Hans", "Becker, Anna")),
            jsonPath("$.collaborators", Matchers.contains("Meier, Karl")),
            jsonPath("$.originators", Matchers.contains("FOO")),
            jsonPath(
                "$.conferenceNotes",
                Matchers.contains("Internationaler Kongress 2025, Berlin, GER")),
            jsonPath("$.languages", Matchers.contains("deu", "eng")),
            jsonPath(
                "$.shortReport",
                Matchers.is("Ein Überblick über die Entwicklung der Rechtsprechung")),
            jsonPath(
                "$.outline", Matchers.is("Teil A: Einführung; Teil B: Praxisfälle; Teil C: Fazit")),
            jsonPath(
                "$.encoding[*]['@id']",
                containsInAnyOrder(
                    "/v1/literature/" + documentNumberPersistedInTest + "/html",
                    "/v1/literature/" + documentNumberPersistedInTest + "/xml",
                    "/v1/literature/" + documentNumberPersistedInTest + "/zip")),
            jsonPath(
                "$.encoding[*].contentUrl",
                containsInAnyOrder(
                    "/v1/literature/" + documentNumberPersistedInTest + ".html",
                    "/v1/literature/" + documentNumberPersistedInTest + ".xml",
                    "/v1/literature/" + documentNumberPersistedInTest + ".zip")));
  }

  @Test
  @DisplayName("Should return not found when using document number is not found")
  void shouldReturnNotFound() throws Exception {

    mockMvc
        .perform(get(ApiConfig.Paths.LITERATURE + "/TEST").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("XML Endpoint Should return error xml when literature not in bucket")
  void shouldReturnNotFoundIfXMLNotPresent() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "/NOT_PRESENT_IN_BUCKET.xml")
                .contentType(MediaType.APPLICATION_XML))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return XML version of a literature item")
  void literatureXMLEndpoint() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "/" + this.documentNumberPresentInBucket + ".xml")
                .contentType(MediaType.APPLICATION_XML))
        .andExpectAll(
            status().isOk(),
            content()
                .string(
                    startsWithIgnoringCase(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")),
            content().contentType("application/xml"));
  }

  @Test
  @DisplayName("Html Endpoint Should return error when literature not in bucket")
  void shouldReturnNotFoundIfHTMLNotPresent() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "/NOT_PRESENT_IN_BUCKET.html")
                .contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return HTML version of literature item")
  void shouldReturnSingleLiteratureHtml() throws Exception {

    String responseContent =
        mockMvc
            .perform(
                get(ApiConfig.Paths.LITERATURE + "/" + this.documentNumberPresentInBucket + ".html")
                    .contentType(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertThat(responseContent)
        .contains(
            "Literatur Test Dokument",
            "I. Äpfel.",
            "1. Dies ist ein literature",
            "Außerdem gib es noch");
  }

  @Test
  @DisplayName("Should return correct item when searching with searchTerm")
  void shouldReturnLiteratureItemWhenUsingSearchTerm() throws Exception {
    final String searchTerm = "Handelsrecht";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + String.format("?searchTerm=%s", searchTerm))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0]['item'].documentNumber", Matchers.is("KALU000000001")))
        .andExpect(jsonPath("$.member[0]['item'].recordingDate", Matchers.is("2013-01-01")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].yearsOfPublication",
                Matchers.containsInAnyOrder("2014", "2024")))
        .andExpect(jsonPath("$.member[0]['item'].documentTypes", Matchers.contains("Auf")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].dependentReferences", Matchers.contains("BUV, 1982, 123-123")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].independentReferences",
                Matchers.contains("50 Jahre Betriebs-Berater, 1987, 123-456")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].headline",
                Matchers.is("Literatur. Einführung in das Handelsrecht")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].alternativeHeadline",
                Matchers.is("Dokumentation Handelsrecht")))
        .andExpect(jsonPath("$.member[0]['item'].authors", Matchers.contains("Musterfrau, Sabine")))
        .andExpect(
            jsonPath("$.member[0]['item'].collaborators", Matchers.contains("Mustermann, Max")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].shortReport",
                Matchers.is("Kurzer Bericht über die Entwicklung des Handelsrechts")))
        .andExpect(
            jsonPath(
                "$.member[0]['item'].outline",
                Matchers.nullValue())) // excluded from search results
        .andExpect(jsonPath("$.member[0]['textMatches'][*]['name']", Matchers.hasItem("mainTitle")))
        .andExpect(
            jsonPath(
                "$.member[0]['textMatches'][*]['text']",
                Matchers.hasItem("Einführung in das <mark>%s</mark>".formatted(searchTerm))));
  }

  @Test
  @DisplayName("Search by document number")
  void shouldReturnItemWhenSearchingByDocumentNumber() throws Exception {
    final String documentNumber = "KALU000000002";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?documentNumber=" + documentNumber)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is(documentNumber)));
  }

  @Test
  @DisplayName("Search by year of publication")
  void shouldReturnItemWhenSearchingByYearOfPublication() throws Exception {
    final String year = "2020";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?yearOfPublication=" + year)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000003")));
  }

  @Test
  @DisplayName("Search by document type")
  void shouldReturnItemWhenSearchingByDocumentType() throws Exception {
    final String documentType = "Buch";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?documentType=" + documentType)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000003")));
  }

  @Test
  @DisplayName("Search by author")
  void shouldReturnItemWhenSearchingByAuthor() throws Exception {
    final String author = "Hoffmann, Clara";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?author=" + author)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000003")));
  }

  @Test
  @DisplayName("Search by collaborator")
  void shouldReturnItemWhenSearchingByCollaborator() throws Exception {
    final String collaborator = "Mustermann, Max";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?collaborator=" + collaborator)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000001")));
  }

  @Test
  @DisplayName("Search by multiple document types")
  void shouldReturnItemsWhenSearchingByMultipleDocumentTypes() throws Exception {
    final String types = "Auf,Buch";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?documentType=" + types)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(
            jsonPath(
                "$.member[*].item.documentNumber",
                Matchers.containsInAnyOrder("KALU000000001", "KALU000000003")));
  }

  @Test
  @DisplayName("Search by multiple authors")
  void shouldReturnItemsWhenSearchingByMultipleAuthors() throws Exception {
    final String authors = "Hoffmann, Clara,Schmidt, Hans";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?author=" + authors)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(2)))
        .andExpect(
            jsonPath(
                "$.member[*].item.documentNumber",
                Matchers.containsInAnyOrder("KALU000000002", "KALU000000003")));
  }

  @Test
  @DisplayName("Search by document type and year of publication")
  void shouldReturnItemWhenSearchingByTypeAndYear() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + "?documentType=Buch&yearOfPublication=2020")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000003")));
  }

  @Test
  @DisplayName("Search by author and collaborator")
  void shouldReturnItemWhenSearchingByAuthorAndCollaborator() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE
                    + "?author=Musterfrau, Sabine&collaborator=Mustermann, Max")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is("KALU000000001")));
  }

  @Test
  @DisplayName("Search by multiple parameters with multiple values")
  void shouldReturnItemsWhenSearchingByMultipleParamsAndValues() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE
                    + "?documentType=Kommentar,Auf&author=Schmidt, Hans,Hoffmann, Clara")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(
            jsonPath(
                "$.member[*].item.documentNumber", Matchers.containsInAnyOrder("KALU000000002")));
  }

  @Test
  @DisplayName("Should return literature items sorted by default")
  void shouldReturnedDefaultSort() throws Exception {
    mockMvc
        .perform(get(ApiConfig.Paths.LITERATURE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(jsonPath("$.member[0]['item'].documentNumber", Matchers.is("KALU000000003")))
        .andExpect(jsonPath("$.member[1]['item'].documentNumber", Matchers.is("KALU000000001")))
        .andExpect(jsonPath("$.member[2]['item'].documentNumber", Matchers.is("KALU000000002")));
  }

  @Test
  @DisplayName("Should return literature items sorted by date asc")
  void shouldReturnElementsSortedByDateAsc() throws Exception {
    final String sortParam = "date";
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + String.format("?sort=%s", sortParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(jsonPath("$.member[0]['item'].documentNumber", Matchers.is("KALU000000002")))
        .andExpect(jsonPath("$.member[1]['item'].documentNumber", Matchers.is("KALU000000001")))
        .andExpect(jsonPath("$.member[2]['item'].documentNumber", Matchers.is("KALU000000003")));
  }

  @Test
  @DisplayName("Should return literature items sorted by date desc")
  void shouldReturnElementsSortedByDateDesc() throws Exception {
    final String sortParam = "-date";
    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE + String.format("?sort=%s", sortParam))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(3)))
        .andExpect(jsonPath("$.member[0]['item'].documentNumber", Matchers.is("KALU000000003")))
        .andExpect(jsonPath("$.member[1]['item'].documentNumber", Matchers.is("KALU000000001")))
        .andExpect(jsonPath("$.member[2]['item'].documentNumber", Matchers.is("KALU000000002")));
  }
}
