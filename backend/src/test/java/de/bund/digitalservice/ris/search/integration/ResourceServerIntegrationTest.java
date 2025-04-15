package de.bund.digitalservice.ris.search.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ResourceServerIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithAnonymousUser
  void givenRequestIsAnonymousThenUnauthorized() throws Exception {
    mockMvc
        .perform(get(ApiConfig.Paths.CASELAW))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithJwt("jwtTokens/ValidAccessToken.json")
  void givenUserHasValidTokenThenOK() throws Exception {

    this.mockMvc.perform(get(ApiConfig.Paths.CASELAW + "?query=test")).andExpect(status().isOk());
  }

  @Test
  @WithJwt("jwtTokens/ValidTesterAccessToken.json")
  void givenTestUserHasValidTokenThenOK() throws Exception {

    this.mockMvc.perform(get(ApiConfig.Paths.CASELAW + "?query=test")).andExpect(status().isOk());
  }
}
