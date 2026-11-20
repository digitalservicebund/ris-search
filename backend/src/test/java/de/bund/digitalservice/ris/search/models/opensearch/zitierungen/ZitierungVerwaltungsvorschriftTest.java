package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ZitierungVerwaltungsvorschriftTest {

  @Test
  void toSearchTextJoinsAllSearchableProperties() {
    ZitierungVerwaltungsvorschrift zitierung =
        ZitierungVerwaltungsvorschrift.builder()
            .richtung(Richtung.PASSIV)
            .referenzUri("uri-is-not-searchable")
            .dokumentnummer("KSNR006800006")
            .artDerZitierung("Anwendung")
            .jurisAbkuerzung("VR BA 16/86")
            .fundstelle(
                Fundstelle.builder()
                    .periodikum("Dienstbl BA")
                    .periodikumTitel("Dienstblatt der Bundesanstalt für Arbeit")
                    .zitatstelle("1986, Nr 16, 1-8")
                    .fundstellenTyp("amtlich")
                    .build())
            .build();

    assertThat(zitierung.toSearchText())
        .isEqualTo(
            "KSNR006800006 Anwendung VR BA 16/86 Dienstbl BA Dienstblatt der Bundesanstalt für"
                + " Arbeit 1986, Nr 16, 1-8 amtlich");
  }

  @Test
  void toSearchTextSkipsMissingFundstelle() {
    ZitierungVerwaltungsvorschrift zitierung =
        ZitierungVerwaltungsvorschrift.builder()
            .dokumentnummer("KSNR008561615")
            .jurisAbkuerzung("VR VDR")
            .build();

    assertThat(zitierung.toSearchText()).isEqualTo("KSNR008561615 VR VDR");
  }
}
