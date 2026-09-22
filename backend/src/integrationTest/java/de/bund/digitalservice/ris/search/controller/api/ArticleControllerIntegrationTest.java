package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.utils.JsonldResultMatchers.isJsonLdCompliant;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.LegislationPartType;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class ArticleControllerIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Autowired private ArticlesRepository repository;

  @BeforeEach
  void setup() {
    repository.deleteAll();
  }

  @Test
  void theRouteIsproperlyParsedAndTheResponseIsJsonldCompliant() throws Exception {
    repository.save(
        Article.builder()
            .id("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/art-z1")
            .eId("art-z1")
            .expressionEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu")
            .workEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/1975-01-01")
            .name("§ 1")
            .entryIntoForceDate(LocalDate.of(1975, Month.JANUARY, 1))
            .expiryDate(null)
            .manifestationEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/regelungstext-1.xml")
            .documentType(LegislationPartType.ARTICLE)
            .documentNumber("DKNR0E80B0026DKNE000100010")
            .build());

    mockMvc
        .perform(
            get(ApiConfig.Paths.ARTICLE_WORK_EXAMPLE
                    + "/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/art-z1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            isJsonLdCompliant(),
            jsonPath(
                "$.@id",
                is(
                    "/v1/article/work-example/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/art-z1?pageIndex=0&size=1")),
            jsonPath("$.member", hasSize(1)),
            jsonPath("$.member[0].eId", is("art-z1")));
  }

  @Test
  void itReturnsEmptyListStatusOkOnMissingDocumentnumberInRepo() throws Exception {
    repository.save(
        Article.builder()
            .id("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/art-z1")
            .eId("art-z1")
            .expressionEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu")
            .workEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/1975-01-01")
            .name("§ 1")
            .entryIntoForceDate(LocalDate.of(1975, Month.JANUARY, 1))
            .expiryDate(null)
            .manifestationEli("eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/regelungstext-1.xml")
            .documentType(LegislationPartType.ARTICLE)
            .build());

    mockMvc
        .perform(
            get(ApiConfig.Paths.ARTICLE_WORK_EXAMPLE
                    + "/eli/bund/bgbl-1/1975/s1000/1975-01-01/1/deu/art-z1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), isJsonLdCompliant(), jsonPath("$.member", hasSize(0)));
  }
}
