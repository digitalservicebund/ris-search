package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Spruchkoerper;
import de.bund.digitalservice.ris.search.models.opensearch.Gericht;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawGerichtMapperTest {

  @Test
  @DisplayName("Maps a Gericht with all its fields")
  void mapsGerichtWithAllFields() {
    RisGericht risGericht =
        RisGericht.builder()
            .gerichtstyp("BFH")
            .gerichtsort("München")
            .gerichtsbarkeit("Finanzgerichtsbarkeit")
            .spruchkoerper(Spruchkoerper.builder().value("1. Senat").build())
            .sitzDerAussenstelle("Kassel")
            .build();

    Gericht gericht = CaseLawGerichtMapper.map(risGericht);

    assertThat(gericht)
        .isEqualTo(
            Gericht.builder()
                .gerichtstyp("BFH")
                .gerichtsort("München")
                .gerichtsbarkeit("Finanzgerichtsbarkeit")
                .spruchkoerper("1. Senat")
                .sitzDerAussenstelle("Kassel")
                .build());
  }

  @Test
  @DisplayName("Maps a Gericht without Spruchkoerper")
  void mapsGerichtWithoutSpruchkoerper() {
    Gericht gericht = CaseLawGerichtMapper.map(RisGericht.builder().gerichtstyp("BGH").build());

    assertThat(gericht).isEqualTo(Gericht.builder().gerichtstyp("BGH").build());
  }

  @Test
  @DisplayName("Returns null when there is no Gericht")
  void returnsNullWithoutGericht() {
    assertThat(CaseLawGerichtMapper.map(null)).isNull();
  }
}
