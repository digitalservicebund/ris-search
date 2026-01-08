package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWithIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class AdministrativeDirectiveControllerApiTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  private final String documentNumberPresentInBucket = "KSNR0000";

  @Test
  @DisplayName("Should return not found when using document number is not found")
  void shouldReturnNotFound() throws Exception {

    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/TEST")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("XML Endpoint Should return error when document not in bucket")
  void shouldReturnNotFoundIfXMLNotPresent() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/NOT_PRESENT_IN_BUCKET.xml")
                .contentType(MediaType.APPLICATION_XML))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return XML version of an administrative directive item")
  void administrativeDirectiveXMLEndpoint() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE
                    + "/"
                    + documentNumberPresentInBucket
                    + ".xml")
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
  @DisplayName("get by document number")
  void shouldReturnItem() throws Exception {
    final String documentNumber = "KSNR0000";

    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "/" + documentNumber)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentNumber", Matchers.is(documentNumber)));
  }

  @Test
  @DisplayName("Search by document number")
  void shouldReturnItemWhenSearchingByDocumentNumber() throws Exception {
    final String documentNumber = "KSNR0000";

    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "?documentNumber=" + documentNumber)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is(documentNumber)));
  }

  @ParameterizedTest(name = "Sorting by ''{0}'' returns data")
  @ValueSource(
      strings = {
        "default",
        "date",
        "-date",
        "DATUM",
        "-DATUM",
        "documentNumber",
        "-documentNumber"
      })
  void shouldReturnItemWhenSortingByValidParameter(String sortParameter) throws Exception {
    final String documentNumber = "KSNR0000";

    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "?sort=" + sortParameter)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.member", hasSize(1)))
        .andExpect(jsonPath("$.member[0].item.documentNumber", Matchers.is(documentNumber)));
  }

  @Test
  @DisplayName("Should return error when sorting by unknown parameter")
  void shouldReturnErrorWhenSortingByUnknownParameter() throws Exception {
    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE + "?sort=foo")
                .contentType(MediaType.APPLICATION_XML))
        .andExpect(status().isUnprocessableContent());
  }

  @Test
  @DisplayName("Should return HTML version of administrative directive item")
  void shouldReturnAdministrativeDirectiveHtml() throws Exception {

    String responseContent =
        mockMvc
            .perform(
                get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE
                        + "/"
                        + documentNumberPresentInBucket
                        + ".html")
                    .contentType(MediaType.TEXT_HTML))
            .andExpectAll(status().isOk(), content().contentType("text/html;charset=UTF-8"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertThat(responseContent).contains("administrative directive test short report");
  }
}
