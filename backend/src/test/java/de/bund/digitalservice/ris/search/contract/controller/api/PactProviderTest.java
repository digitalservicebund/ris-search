package de.bund.digitalservice.ris.search.contract.controller.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import de.bund.digitalservice.ris.search.controller.api.CaseLawController;
import de.bund.digitalservice.ris.search.controller.api.NormsController;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@Provider("backend")
@PactFolder("pacts")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
@WithJwt("jwtTokens/ValidAccessToken.json")
class PactProviderTest extends ContainersIntegrationBase {
  @Autowired private CaseLawRepository caseLawRepository;
  @Autowired private CaseLawController caseLawController;
  @Autowired private NormsRepository normsRepository;
  @Autowired private NormsController normsController;
  @Autowired private MockMvc mockMvc;

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @BeforeEach
  void before(PactVerificationContext context) {
    MockMvcTestTarget testTarget = new MockMvcTestTarget(mockMvc);
    testTarget.setControllers(caseLawController, normsController);
    context.setTarget(testTarget);
  }

  @State("I have a document in the database with number 12345")
  public void thereIsDocument12345() throws IOException {
    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    var caseLawTestOne =
        CaseLawDocumentationUnit.builder()
            .id("decision-123")
            .documentNumber("12345")
            .ecli("ECLI:DE:FGRLP:1969:0905.IV85.68.0A")
            .courtType("FG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(LocalDate.of(2023, 10, 11))
            .guidingPrinciple("Leitsatz")
            .otherLongText("Sonstiger Langtext")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .caseFacts("Tatbestand")
            .publicationStatus(PublicationStatus.UNPUBLISHED.toString())
            .documentationOffice("DS")
            .decisionGrounds("Entscheidungsgrunde")
            .grounds("Grunde")
            .dissentingOpinion("Abweichende Meinung")
            .headline("Uberschrift")
            .headnote("Orientierungssatz")
            .tenor("Tenor")
            .decisionDate(LocalDate.of(2023, 10, 11))
            .fileNumbers(List.of("BGH 123/23", "BGH 124/23"))
            .courtType("FG")
            .location("Berlin")
            .documentType("Urteil")
            .outline("Leitsatz")
            .judicialBody("Gericht")
            .keywords(List.of("keyword1", "keyword2", "keyword3"))
            .courtKeyword("Bundesgerichtshof")
            .decisionName(List.of("Decision Name 1", "Decision Name 2"))
            .deviatingDocumentNumber(List.of("DEV-123", "DEV-124"))
            .error(false)
            .build();

    caseLawRepository.saveAll(List.of(caseLawTestOne));
  }

  @State(
      "I have a document in the database with eli/bund/bgbl-1/2000/s998/2000-10-06/2/deu/regelungstext-1")
  public void thereIsDocumentWithValidEli() throws IOException {
    assertTrue(openSearchContainer.isRunning());

    super.recreateIndex();
    super.updateMapping();

    Norm testNorm =
        Norm.builder()
            .id("norm-id")
            .tableOfContents(NormsTestData.nestedToC)
            .workEli("eli/bund/bgbl-1/2000/s998/regelungstext-1")
            .expressionEli("eli/bund/bgbl-1/2000/s998/2000-10-06/2/deu/regelungstext-1")
            .manifestationEliExample(
                "eli/bund/bgbl-1/2000/s998/2000-10-06/2/deu/2000-10-06/regelungstext-1.xml")
            .officialTitle(
                "Verordnung uber die Nichtanwendung fleisch- und geflugelfleischhygienerechtlicher Vorschriften Artikel 6 der Funften Verordnung zur Anderung von Vorschriften zum Schutz der Verbraucher vor der Bovinen Spongiformen Enzephalopathie")
            .officialShortTitle(
                "Verordnung uber die Nichtanwendung fleisch- und geflugelfleischhygienerechtlicher Vorschriften")
            .normsDate(LocalDate.of(1977, 5, 21))
            .datePublished(LocalDate.of(1977, 5, 22))
            .entryIntoForceDate(LocalDate.of(2001, 5, 29))
            .articles(
                List.of(
                    Article.builder()
                        .name("1 Fleischhygiene-Verordnung")
                        .eId("hauptteil-1_art-1")
                        .build(),
                    Article.builder()
                        .name("2 Geflugelfleischhygiene-Verordnung")
                        .eId("hauptteil-1_art-2")
                        .build()))
            .officialAbbreviation("Fleischhygiene-Verordnung")
            .publishedIn("Bundesgesetzblatt Teil I")
            .build();

    normsRepository.saveAll(List.of(testNorm));
  }
}
