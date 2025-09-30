package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithJwt("jwtTokens/ValidAccessToken.json")
@Tag("integration")
class ReindexingControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;

  @Test
  void syncCaselawSuccess() throws Exception {
    mockMvc.perform(post(ApiConfig.Paths.SYNC_CASELAW).with(csrf())).andExpect(status().isOk());
  }

  @Test
  void syncNormSuccess() throws Exception {
    mockMvc.perform(post(ApiConfig.Paths.SYNC_NORMS).with(csrf())).andExpect(status().isOk());
  }

  @Test
  void syncLiteratureSuccess() throws Exception {
    mockMvc.perform(post(ApiConfig.Paths.SYNC_LITERATURE).with(csrf())).andExpect(status().isOk());
  }
}
