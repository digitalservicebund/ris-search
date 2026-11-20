package de.bund.digitalservice.ris.search.models.opensearch.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GerichtTest {

  @Test
  void toSearchTextJoinsAllProperties() {
    Gericht gericht =
        Gericht.builder()
            .gerichtstyp("LArbG")
            .gerichtsort("Kiel")
            .gerichtsbarkeit("Arbeitsgerichtsbarkeit")
            .spruchkoerper("3. Kammer")
            .sitzDerAussenstelle("Lübeck")
            .build();

    assertThat(gericht.toSearchText())
        .isEqualTo("LArbG Kiel Arbeitsgerichtsbarkeit 3. Kammer Lübeck");
  }
}
