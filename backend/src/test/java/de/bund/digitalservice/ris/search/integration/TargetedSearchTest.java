package de.bund.digitalservice.ris.search.integration;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class TargetedSearchTest extends ContainersIntegrationBase {

  @Test
  @DisplayName("Celex targeted search works as expected")
  void celexTargetSearchWorksAsExpected() {
    var caselaws = getAll(caseLawRepository, e -> StringUtils.isNotEmpty(e.celex()));
    var firstCL = caselaws.getFirst();
    var lastCL = caselaws.getLast();

    assertThat(searchAll(firstCL.celex()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchAll(lastCL.celex()).getFirst().getId()).isEqualTo(lastCL.id());
    assertThat(searchCaseLaw(firstCL.celex()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchCaseLaw(lastCL.celex()).getFirst().getId()).isEqualTo(lastCL.id());
  }

  @Test
  @DisplayName("Doc number targeted search works as expected")
  void docNumberTargetSearchWorksAsExpected() {
    var caselaws = getAll(caseLawRepository, e -> StringUtils.isNotEmpty(e.documentNumber()));
    var firstCL = caselaws.getFirst();
    var lastCL = caselaws.getLast();

    assertThat(searchAll(firstCL.documentNumber()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchAll(lastCL.documentNumber()).getFirst().getId()).isEqualTo(lastCL.id());
    assertThat(searchCaseLaw(firstCL.documentNumber()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchCaseLaw(lastCL.documentNumber()).getFirst().getId()).isEqualTo(lastCL.id());
  }

  @Test
  @DisplayName("ECLI targeted search works as expected")
  void ecliTargetSearchWorksAsExpected() {
    var caselaws = getAll(caseLawRepository, e -> StringUtils.isNotEmpty(e.ecli()));
    var firstCL = caselaws.getFirst();
    var lastCL = caselaws.getLast();

    assertThat(searchAll(firstCL.ecli()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchAll(lastCL.ecli()).getFirst().getId()).isEqualTo(lastCL.id());
    assertThat(searchCaseLaw(firstCL.ecli()).getFirst().getId()).isEqualTo(firstCL.id());
    assertThat(searchCaseLaw(lastCL.ecli()).getFirst().getId()).isEqualTo(lastCL.id());
  }

  @Test
  @DisplayName("File number targeted search works as expected")
  void fileNumberTargetSearchWorksAsExpected() {
    var caselaws = getAll(caseLawRepository, e -> !CollectionUtils.isEmpty(e.fileNumbers()));
    var firstCL = caselaws.getFirst();
    var lastCL = caselaws.getLast();

    assertThat(searchAll(firstCL.fileNumbers().getFirst()).getFirst().getId())
        .isEqualTo(firstCL.id());
    assertThat(searchAll(lastCL.fileNumbers().getFirst()).getFirst().getId())
        .isEqualTo(lastCL.id());
    assertThat(searchCaseLaw(firstCL.fileNumbers().getFirst()).getFirst().getId())
        .isEqualTo(firstCL.id());
    assertThat(searchCaseLaw(lastCL.fileNumbers().getFirst()).getFirst().getId())
        .isEqualTo(lastCL.id());
  }

  @Test
  @DisplayName("Abweichende Eclis targeted search works as expected")
  void abweichendeEclisTargetSearchWorksAsExpected() {
    // ECLI:DE:FGHH:1972:0630.III10.72.1 is a primary ecli for BFRE000157357, in the abweichende
    // ecli for BFRE000087655 and in the leitsatz for BFRE000107055
    var allDocResults = searchAll("ECLI:DE:FGHH:1972:0630.III10.72.1");
    assertThat(allDocResults.get(0).getId()).isEqualTo("BFRE000157357");
    assertThat(allDocResults.get(1).getId()).isEqualTo("BFRE000087655");
    assertThat(allDocResults.get(2).getId()).isEqualTo("BFRE000107055");

    var caseLawResults = searchCaseLaw("ECLI:DE:FGHH:1972:0630.III10.72.1");
    assertThat(caseLawResults.get(0).getId()).isEqualTo("BFRE000157357");
    assertThat(caseLawResults.get(1).getId()).isEqualTo("BFRE000087655");
    assertThat(caseLawResults.get(2).getId()).isEqualTo("BFRE000107055");
  }

  @Test
  @DisplayName("Abweichende file numbers targeted search works as expected")
  void abweichendeFileNumbersTargetSearchWorksAsExpected() {
    // file_number_7 is a file number for BFRE000157358, in the abweichende Aktenzeichen for
    // BFRE000157356 and in the headline for BFRE000157357
    var allDocResults = searchAll("file_number_7");
    assertThat(allDocResults.get(0).getId()).isEqualTo("BFRE000157358");
    assertThat(allDocResults.get(1).getId()).isEqualTo("BFRE000157356");
    assertThat(allDocResults.get(2).getId()).isEqualTo("BFRE000157357");

    var caseLawResults = searchCaseLaw("file_number_7");
    assertThat(caseLawResults.get(0).getId()).isEqualTo("BFRE000157358");
    assertThat(caseLawResults.get(1).getId()).isEqualTo("BFRE000157356");
    assertThat(caseLawResults.get(2).getId()).isEqualTo("BFRE000157357");
  }
}
