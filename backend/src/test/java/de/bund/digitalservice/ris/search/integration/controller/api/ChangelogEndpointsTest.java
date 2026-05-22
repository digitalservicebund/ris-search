package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class ChangelogEndpointsTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CaseLawBucket caseLawBucket;

  @Autowired NormsBucket normsBucket;

  @Autowired LiteratureBucket literatureBucket;

  @Autowired AdministrativeDirectiveBucket administrativeDirectiveBucket;

  private void saveDefaultChangelog(ObjectStorage bucket) throws JsonProcessingException {
    Changelog changelog =
        new Changelog(
            new HashSet<>(List.of("file1/file1.xml")),
            new HashSet<>(List.of("file2/file2.xml")),
            false);
    String changelogContent = new ObjectMapper().writeValueAsString(changelog);

    bucket.save(
        ChangelogService.CHANGELOGS_PREFIX + "2026-07-03T12:00:00.276525407Z", changelogContent);
  }

  @Test
  void caselawChangelogsCanGetQueried() throws Exception {
    saveDefaultChangelog(caseLawBucket);
    String from = "2026-07-03T12:00:00Z";
    String to = "2026-07-04T12:00:00Z";

    mockMvc
        .perform(
            get(ApiConfig.Paths.CASELAW_CHANGELOGS)
                .params(MultiValueMap.fromSingleValue(Map.of("from", from, "to", to))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.changed[0].['@id']").value("/v1/case-law/file1"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("Decision"))
        .andExpect(jsonPath("$.deleted[0].['@id']").value("/v1/case-law/file2"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("Decision"));
  }

  @Test
  void literatureChangelogsCanGetQueried() throws Exception {
    saveDefaultChangelog(literatureBucket);
    String from = "2026-07-03T12:00:00Z";
    String to = "2026-07-04T12:00:00Z";

    mockMvc
        .perform(
            get(ApiConfig.Paths.LITERATURE_CHANGELOGS)
                .params(MultiValueMap.fromSingleValue(Map.of("from", from, "to", to))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.changed[0].['@id']").value("/v1/literature/file1"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("Literature"))
        .andExpect(jsonPath("$.deleted[0].['@id']").value("/v1/literature/file2"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("Literature"));
  }

  @Test
  void administrativeDirectiveChangelogsCanGetQueried() throws Exception {
    saveDefaultChangelog(administrativeDirectiveBucket);
    String from = "2026-07-03T12:00:00Z";
    String to = "2026-07-04T12:00:00Z";

    mockMvc
        .perform(
            get(ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE_CHANGELOGS)
                .params(MultiValueMap.fromSingleValue(Map.of("from", from, "to", to))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.changed[0].['@id']").value("/v1/administrative-directive/file1"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("AdministrativeDirective"))
        .andExpect(jsonPath("$.deleted[0].['@id']").value("/v1/administrative-directive/file2"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("AdministrativeDirective"));
  }

  @Test
  void legislationChangelogsCanGetQueried() throws Exception {
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
        .andExpect(
            jsonPath("$.changed[0].['@id']")
                .value("/v1/legislation/eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu"))
        .andExpect(jsonPath("$.changed[0].['@type']").value("Legislation"))
        .andExpect(
            jsonPath("$.deleted[0].['@id']")
                .value("/v1/legislation/eli/bund/bgbl-1/2000/identifier/2026-01-01/1/deu"))
        .andExpect(jsonPath("$.deleted[0].['@type']").value("Legislation"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ApiConfig.Paths.ADMINISTRATIVE_DIRECTIVE_CHANGELOGS,
        ApiConfig.Paths.LEGISLATION_CHANGELOGS,
        ApiConfig.Paths.CASELAW_CHANGELOGS,
        ApiConfig.Paths.LITERATURE_CHANGELOGS
      })
  void queryParamtersAreMandatory(String route) throws Exception {
    mockMvc.perform(get(route)).andExpect(status().is4xxClientError());
  }
}
