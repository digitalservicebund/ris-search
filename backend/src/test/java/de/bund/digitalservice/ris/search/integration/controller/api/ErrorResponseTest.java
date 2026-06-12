package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.config.TestMockEndpointsController;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestMockEndpointsController.class)
@Tag("integration")
class ErrorResponseTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return 403 when using wrong http method")
  void shouldReturn403() throws Exception {

    mockMvc
        .perform(get("/forbidden").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("forbidden")))
        .andExpect(
            jsonPath(
                "$.errors[0].message",
                Matchers.is(
                    "Access is not allowed. This includes some cases of improper access such as wrong method.")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is("")));
  }

  @Test
  @DisplayName("Should return 404 when using document number is not found")
  void shouldReturn404() throws Exception {
    mockMvc
        .perform(get("/DOESNOTEXIST").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("not_found")))
        .andExpect(
            jsonPath("$.errors[0].message", Matchers.is("The requested data could not be found")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is("")));
  }

  @Test
  @DisplayName("Should return our custom 500 error response on generic exception")
  void shouldReturn500() throws Exception {
    mockMvc
        .perform(get("/internalError").contentType(MediaType.TEXT_HTML))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("internal_error")))
        .andExpect(
            jsonPath(
                "$.errors[0].message",
                Matchers.is("An unexpected error occurred. Please try again later.")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is("")));
  }

  @Test
  @DisplayName("Should return 400 on argument type mismatch")
  void shouldReturn400MethodArgumentTypeMismatchException() throws Exception {
    mockMvc
        .perform(
            get("/v1/legislation/eli/bund/bgbl-1/2002/s42/regelungstext-1/abschnitt/§-1629")
                .contentType(MediaType.TEXT_HTML))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.errors[0].code", Matchers.is("bad_request")))
        .andExpect(jsonPath("$.errors[0].message", Matchers.is("The request could not be parsed")))
        .andExpect(jsonPath("$.errors[0].parameter", Matchers.is("")));
  }

  @Test
  void shouldGracefullyHandleClientAbortExceptions() throws Exception {
    mockMvc
        .perform(get("/clientAbort").contentType(MediaType.TEXT_HTML))
        .andExpect(status().isOk());
  }

  @Test
  void shouldRethrowHttpMessageNotWritableExceptionThatAreNotFromClientAbort() throws Exception {
    mockMvc
        .perform(get("/throwHttpMessageNotWritableException").contentType(MediaType.TEXT_HTML))
        .andExpect(status().is5xxServerError());
  }
}
