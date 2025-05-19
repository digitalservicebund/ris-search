package de.bund.digitalservice.ris.search.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/*
 * Advanced search functionality is disabled in this test so it doesn't show up in the generated openapi.json file.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "feature-flags.advanced-search=false",
      "swagger.server.url=https://testphase.rechtsinformationen.bund.de",
      "swagger.server.description=Public API (preview)"
    })
@AutoConfigureMockMvc
@Tag("integration")
class SwaggerIntegrationTest extends ContainersIntegrationBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldExposeOpenApiEndpoint() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andDo(
            item -> {
              byte[] content = item.getResponse().getContentAsByteArray();

              Path outDir = Paths.get("./out");
              Files.createDirectories(outDir);
              File outputFile = new File(outDir.toFile(), "openapi.json");
              try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(content);
              }
            })
        .andExpect(
            jsonPath(
                "$.components.schemas.LegislationWorkSchema.properties['@type'].example",
                Matchers.is("Legislation")))
        .andExpect(
            jsonPath(
                "$.tags[*].name",
                Matchers.containsInAnyOrder("All documents", "Legislation", "Case Law")));
  }
}
