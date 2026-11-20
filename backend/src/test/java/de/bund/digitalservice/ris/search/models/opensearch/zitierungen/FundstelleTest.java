package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FundstelleTest {

  @Test
  void toSearchTextJoinsAllProperties() {
    Fundstelle fundstelle =
        Fundstelle.builder()
            .periodikum("StW")
            .periodikumTitel("Die Information über Steuer und Wirtschaft")
            .zitatstelle("1999, 641")
            .fundstellenTyp("nichtamtlich")
            .build();

    assertThat(fundstelle.toSearchText())
        .isEqualTo("StW Die Information über Steuer und Wirtschaft 1999, 641 nichtamtlich");
  }
}
