package de.bund.digitalservice.ris.search.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CaseLawGoneControllerIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return 410 with the new endpoint for the old case-law base path")
  void shouldReturnGoneForBasePath() throws Exception {
    mockMvc
        .perform(get("/v1/case-law"))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("gone")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG)))
        .andExpect(
            jsonPath(
                "$.errors[0].message", Matchers.containsString(ApiConfig.Paths.RECHTSPRECHUNG)));
  }

  @Test
  @DisplayName("Should return 410 with incorrect accept header ")
  void shouldReturn410DisregardingAcceptHeader() throws Exception {
    mockMvc
        .perform(get("/v1/case-law").accept(MediaType.TEXT_HTML))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("gone")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG)))
        .andExpect(
            jsonPath(
                "$.errors[0].message", Matchers.containsString(ApiConfig.Paths.RECHTSPRECHUNG)));
  }

  @Test
  @DisplayName("Should preserve query parameters in the new endpoint for the old base path")
  void shouldReturnGoneForBasePathWithQueryParameters() throws Exception {
    mockMvc
        .perform(get("/v1/case-law?searchTerm=test"))
        .andExpect(status().isGone())
        .andExpect(
            jsonPath(
                "$.errors[0].parameter",
                Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG + "?searchTerm=test")));
  }

  @Test
  @DisplayName("Should return 410 with the new endpoint for old case-law document paths")
  void shouldReturnGoneForDocumentPath() throws Exception {
    mockMvc
        .perform(get("/v1/case-law/BFRE000107055.html"))
        .andExpect(status().isGone())
        .andExpect(
            jsonPath(
                "$.errors[0].parameter",
                Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG + "/BFRE000107055.html")));
  }

  @Test
  @DisplayName("Should return 410 with the new endpoint for old nested case-law attachment paths")
  void shouldReturnGoneForNestedAttachmentPath() throws Exception {
    mockMvc
        .perform(get("/v1/case-law/BFRE000107055/Attachment.png"))
        .andExpect(status().isGone())
        .andExpect(
            jsonPath(
                "$.errors[0].parameter",
                Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG + "/BFRE000107055/Attachment.png")));
  }

  @Test
  @DisplayName("Should return 410 with the new endpoint for the old case-law changelog path")
  void shouldReturnGoneForChangelogPath() throws Exception {
    mockMvc
        .perform(get("/v1/case-law/changelog"))
        .andExpect(status().isGone())
        .andExpect(
            jsonPath(
                "$.errors[0].parameter", Matchers.is(ApiConfig.Paths.RECHTSPRECHUNG_CHANGELOGS)));
  }
}
