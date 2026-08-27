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
}
