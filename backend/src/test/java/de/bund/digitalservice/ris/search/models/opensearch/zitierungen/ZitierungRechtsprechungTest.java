package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.opensearch.common.Gericht;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class ZitierungRechtsprechungTest {

  @Test
  void toSearchTextJoinsAllPartsWithGermanDate() {
    ZitierungRechtsprechung zitierung =
        ZitierungRechtsprechung.builder()
            .richtung(Richtung.AKTIV)
            .referenzUri("uri-is-not-searchable")
            .dokumentnummer("BVRE100338409")
            .artDerZitierung("Vergleiche")
            .gericht(
                Gericht.builder()
                    .gerichtstyp("VerfGH")
                    .gerichtsort("München")
                    .gerichtsbarkeit("Verfassungsgerichtsbarkeit")
                    .spruchkoerper("1. Senat")
                    .sitzDerAussenstelle("Nürnberg")
                    .build())
            .entscheidungsdatum(LocalDate.of(1983, Month.DECEMBER, 2))
            .dokumenttyp("Beschluss")
            .aktenzeichen("Vf. 94-VI-81")
            .fundstelle(
                Fundstelle.builder()
                    .periodikum("BayVBl")
                    .periodikumTitel("Bayerische Verwaltungsblätter")
                    .zitatstelle("1984, 15")
                    .fundstellenTyp("amtlich")
                    .build())
            .build();

    assertThat(zitierung.toSearchText())
        .isEqualTo(
            "BVRE100338409 Vergleiche VerfGH München Verfassungsgerichtsbarkeit 1. Senat Nürnberg"
                + " 02.12.1983 Beschluss Vf. 94-VI-81 BayVBl Bayerische Verwaltungsblätter 1984,"
                + " 15 amtlich");
  }

  @Test
  void toSearchTextSkipsMissingGerichtDateAndFundstelle() {
    ZitierungRechtsprechung zitierung =
        ZitierungRechtsprechung.builder()
            .dokumentnummer("JURE100074208")
            .aktenzeichen("3 Sa")
            .build();

    assertThat(zitierung.toSearchText()).isEqualTo("JURE100074208 3 Sa");
  }
}
