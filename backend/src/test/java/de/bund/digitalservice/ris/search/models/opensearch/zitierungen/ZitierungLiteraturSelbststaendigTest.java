package de.bund.digitalservice.ris.search.models.opensearch.zitierungen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ZitierungLiteraturSelbststaendigTest {

  @Test
  void searchTextJoinsAllSearchableProperties() {
    ZitierungLiteraturSelbststaendig zitierung =
        ZitierungLiteraturSelbststaendig.builder()
            .richtung(Richtung.AKTIV)
            .referenzUri("uri-is-not-searchable")
            .dokumentnummer("KSLS071671727")
            .autor("Arnauld, Andreas von")
            .titel("Die Freiheitsrechte und ihre Schranken")
            .veroeffentlichungsjahr("1999")
            .build();

    assertThat(zitierung.searchText())
        .isEqualTo(
            "KSLS071671727 Arnauld, Andreas von Die Freiheitsrechte und ihre Schranken 1999");
  }

  @Test
  void searchTextSkipsMissingParts() {
    ZitierungLiteraturSelbststaendig zitierung =
        ZitierungLiteraturSelbststaendig.builder()
            .dokumentnummer("KSLS071671727")
            .titel("Die Grenzen der Freiheit")
            .build();

    assertThat(zitierung.searchText()).isEqualTo("KSLS071671727 Die Grenzen der Freiheit");
  }
}
