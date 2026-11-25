package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Richtung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ZitierungenMapperTest {

  @Test
  @DisplayName("Returns empty lists for missing references")
  void returnsEmptyListsForNull() {
    assertThat(ZitierungenMapper.mapRechtsprechung(null)).isEmpty();
    assertThat(ZitierungenMapper.mapLiteraturSelbststaendig(null)).isEmpty();
    assertThat(ZitierungenMapper.mapLiteraturUnselbststaendig(null)).isEmpty();
    assertThat(ZitierungenMapper.mapVerwaltungsvorschriften(null)).isEmpty();
  }

  @Test
  @DisplayName("Maps a Rechtsprechung citation without court, date and Fundstelle")
  void mapsRechtsprechungWithMissingOptionalFields() {
    var referenz = new ReferenzRechtsprechung();
    referenz.setRichtung("passiv");
    referenz.setDokumentnummer("JURE100074208");

    List<ZitierungRechtsprechung> zitierungen =
        ZitierungenMapper.mapRechtsprechung(List.of(referenz));

    assertThat(zitierungen)
        .containsExactly(
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.PASSIV)
                .dokumentnummer("JURE100074208")
                .build());
  }

  @Test
  @DisplayName("Keeps aktiv and passiv citations in one list with their Richtung and skips nulls")
  void mapsAllRichtungenIntoOneList() {
    var aktiv = new ReferenzRechtsprechung();
    aktiv.setRichtung("aktiv");
    aktiv.setDokumentnummer("BVRE100338409");
    var passiv = new ReferenzRechtsprechung();
    passiv.setRichtung("passiv");
    passiv.setDokumentnummer("JURE100074208");

    List<ZitierungRechtsprechung> zitierungen =
        ZitierungenMapper.mapRechtsprechung(Arrays.asList(null, aktiv, passiv));

    assertThat(zitierungen)
        .containsExactly(
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.AKTIV)
                .dokumentnummer("BVRE100338409")
                .build(),
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.PASSIV)
                .dokumentnummer("JURE100074208")
                .build());
  }

  @Test
  @DisplayName("Drops citations with a missing or unknown Richtung")
  void dropsCitationsWithInvalidRichtung() {
    var unknown = new ReferenzRechtsprechung();
    unknown.setRichtung("unbekannt");
    unknown.setDokumentnummer("BVRE100338409");
    var missing = new ReferenzRechtsprechung();
    missing.setDokumentnummer("KORE300002025");
    var valid = new ReferenzRechtsprechung();
    valid.setRichtung("aktiv");
    valid.setDokumentnummer("JURE100074208");

    List<ZitierungRechtsprechung> zitierungen =
        ZitierungenMapper.mapRechtsprechung(List.of(unknown, missing, valid));

    assertThat(zitierungen)
        .containsExactly(
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.AKTIV)
                .dokumentnummer("JURE100074208")
                .build());
  }

  @Test
  @DisplayName("Maps a Fundstelle without Periodikum")
  void mapsFundstelleWithoutPeriodikum() {
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.Fundstelle();
    fundstelle.setZitatstelle("1999, 641");
    var referenz = new ReferenzUnselbststaendigeLiteratur();
    referenz.setRichtung("passiv");
    referenz.setDokumentnummer("STLU991393280");
    referenz.setFundstelle(fundstelle);

    List<ZitierungLiteraturUnselbststaendig> zitierungen =
        ZitierungenMapper.mapLiteraturUnselbststaendig(List.of(referenz));

    assertThat(zitierungen)
        .containsExactly(
            ZitierungLiteraturUnselbststaendig.builder()
                .richtung(Richtung.PASSIV)
                .dokumentnummer("STLU991393280")
                .fundstelle(Fundstelle.builder().zitatstelle("1999, 641").build())
                .build());
  }
}
