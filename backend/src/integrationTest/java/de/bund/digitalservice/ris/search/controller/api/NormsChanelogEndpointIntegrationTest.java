package de.bund.digitalservice.ris.search.controller.api;

import static de.bund.digitalservice.ris.utils.JsonldResultMatchers.isJsonLdCompliant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NormsChanelogEndpointIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void itReturnsFileChangesBetweenTimestamps() throws Exception {
    Changelog changelog =
        new Changelog(
            new HashSet<>(
                List.of(
                    "eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu/2026-01-01/regelungstext-verkuendung-1.xml")),
            new HashSet<>(
                List.of(
                    "eli/bund/bgbl-1/2000/identifier/2026-01-01/1/deu/2026-01-01/regelungstext-verkuendung-1.xml")),
            false);
    String changelogContent = new ObjectMapper().writeValueAsString(changelog);
    normsBucket.save(
        ChangelogService.CHANGELOGS_PREFIX + "2026-07-03T12:00:00.276525407Z", changelogContent);

    String from = "2026-07-03T12:00:00Z";
    String to = "2026-07-04T12:00:00Z";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LEGISLATION_CHANGELOGS)
                .params(MultiValueMap.fromSingleValue(Map.of("from", from, "to", to))))
        .andExpect(status().isOk())
        .andExpect(isJsonLdCompliant())
        .andExpect(
            jsonPath("$.changed[0].['@id']")
                .value(
                    "/v1/legislation/eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu/2026-01-01/zip"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("LegislationObject"))
        .andExpect(
            jsonPath("$.deleted[0].['@id']")
                .value("/v1/legislation/eli/bund/bgbl-1/2000/identifier/2026-01-01/1/deu"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("Legislation"));
  }
}
