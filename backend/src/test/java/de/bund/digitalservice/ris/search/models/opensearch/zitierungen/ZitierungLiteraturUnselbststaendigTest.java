package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ZitierungLiteraturUnselbststaendigTest {

  @Test
  void searchTextJoinsAllSearchableProperties() {
    ZitierungLiteraturUnselbststaendig zitierung =
        ZitierungLiteraturUnselbststaendig.builder()
            .richtung(Richtung.AKTIV)
            .referenzUri("uri-is-not-searchable")
            .dokumentnummer("SBLU000539216")
            .autor("Knopp, Lothar")
            .dokumenttyp("Aufsatz")
            .fundstelle(
                Fundstelle.builder()
                    .periodikum("NVwZ")
                    .periodikumTitel("Neue Zeitschrift für Verwaltungsrecht")
                    .zitatstelle("2016, 1139-1141")
                    .fundstellenTyp("nichtamtlich")
                    .build())
            .build();

    assertThat(zitierung.searchText())
        .isEqualTo(
            "SBLU000539216 Knopp, Lothar Aufsatz NVwZ Neue Zeitschrift für Verwaltungsrecht 2016,"
                + " 1139-1141 nichtamtlich");
  }

  @Test
  void searchTextSkipsMissingFundstelle() {
    ZitierungLiteraturUnselbststaendig zitierung =
        ZitierungLiteraturUnselbststaendig.builder()
            .dokumentnummer("STLU991393280")
            .autor("Schmidt-Liebig")
            .build();

    assertThat(zitierung.searchText()).isEqualTo("STLU991393280 Schmidt-Liebig");
  }
}
