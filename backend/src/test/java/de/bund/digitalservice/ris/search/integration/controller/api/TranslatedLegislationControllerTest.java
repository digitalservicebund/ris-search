package de.bund.digitalservice.ris.search.integration.controller.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class TranslatedLegislationControllerTest extends ContainersIntegrationBase {
  @Autowired private MockMvc mockMvc;
  @Autowired private PortalBucket portalBucket;

  @BeforeEach
  void setUpTranslatedLegislationControllerTest() {

    String translationsJson =
        """
                    [{
                             "abbreviation": "Abcd",
                             "name": "Alphabet Act",
                             "filename": "englisch_abcd.html",
                             "german_name": "Alphabet Gesetz",
                             "translator": "Translation provided by someone and someone.",
                             "version": "Version information: The translation includes the amendment(s) to the Act by Article 4 of the Act of 7 October 2003 (Federal Law Gazette I, p. 46)"
                         },
                         {
                             "abbreviation": "efg",
                             "name": "Another Act",
                             "filename": "englisch_efg.html",
                             "german_name": "Anderes Gesetz",
                             "translator": "Translation provided by someone",
                             "version": "Version information: Another Act as published on 13 August 1921 (Federal Law Gazette I, p. 2010)"
                         }]
                    """;

    String htmlOne =
        """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8" />
                      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                      <title>Alphabet Act</title>
                    </head>
                    <body>
                      <p style="text-align: center; font-weight: bold">Alphabet Act</p>
                      <p style="text-align: center; font-weight: bold">(Alphabet Gesetz – ABCD)</p>
                    </body>
                    </html>

                    """;

    String htmlTwo =
        """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8" />
                      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                      <title>Another Act</title>
                    </head>
                    <body>
                      <p style="text-align: center; font-weight: bold">Another Act</p>
                      <p style="text-align: center; font-weight: bold">(Anderes Gesetz – EFG)</p>
                    </body>
                    </html>

                    """;

    portalBucket.save("translations/data.json", translationsJson);
    portalBucket.save("translations/englisch_abcd.html", htmlOne);
    portalBucket.save("translations/englisch_efg.html", htmlTwo);
  }

  @Test
  @DisplayName("Should return list of translation")
  void shouldReturnListOfTranslations() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(ApiConfig.Paths.LEGISLATION_TRANSLATION)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(2)),
            jsonPath("$[0].@id", is("Abcd")),
            jsonPath("$[1].@id", is("efg")));
  }

  @Test
  @DisplayName("Should return list of translation with one item on match")
  void shouldReturnOneEntryInListOnIdMatch() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(ApiConfig.Paths.LEGISLATION_TRANSLATION + "?id=Abcd")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), jsonPath("$", hasSize(1)), jsonPath("$[0].@id", is("Abcd")));
  }

  @Test
  @DisplayName("Should return empty list of translation on no match")
  void shouldReturnEmptyListOnNoIdMatch() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(ApiConfig.Paths.LEGISLATION_TRANSLATION + "?id=hij")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("Should return html of translation")
  void shouldReturnHtmlOfTranslation() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    ApiConfig.Paths.LEGISLATION_TRANSLATION + "/englisch_abcd.html")
                .contentType(MediaType.TEXT_HTML))
        .andExpectAll(
            status().isOk(),
            content().contentTypeCompatibleWith(MediaType.TEXT_HTML),
            content().string(containsString("<title>Alphabet Act</title>")));
  }

  @Test
  @DisplayName("Should return 404 when file does not exist")
  void shouldReturnNotFoundWhenFileDoesNotExist() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    ApiConfig.Paths.LEGISLATION_TRANSLATION + "/englisch_hij.html")
                .contentType(MediaType.TEXT_HTML))
        .andExpectAll(status().isNotFound());
  }
}
