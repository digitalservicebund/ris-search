package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.Richtung;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzNorm;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungLiteraturUnselbststaendig;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ZitierungRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Zitierungen;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawZitierungenMapperTest {

  @Test
  @DisplayName("Returns empty lists when the document has no analysis")
  void returnsEmptyListsWithoutAnalysis() {
    Zitierungen zitierungen = CaseLawZitierungenMapper.map(Meta.builder().build());

    assertThat(zitierungen.zitierungenRechtsprechung()).isEmpty();
    assertThat(zitierungen.zitierungenLiteraturSelbststaendig()).isEmpty();
    assertThat(zitierungen.zitierungenLiteraturUnselbststaendig()).isEmpty();
    assertThat(zitierungen.zitierungenVerwaltungsvorschriften()).isEmpty();
  }

  @Test
  @DisplayName("Returns empty lists when the references contain no citations")
  void returnsEmptyListsForNonCitationReferences() {
    ImplicitReference normReference = new ImplicitReference();
    normReference.setReferenzNorm(new ReferenzNorm());
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(normReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.zitierungenRechtsprechung()).isEmpty();
  }

  @Test
  @DisplayName("Maps a Rechtsprechung citation without court, date and Fundstelle")
  void mapsRechtsprechungWithMissingOptionalFields() {
    var referenz = new ReferenzRechtsprechung();
    referenz.setRichtung("passiv");
    referenz.setDokumentnummer("JURE100074208");
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setReferenzRechtsprechung(referenz);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(implicitReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.zitierungenRechtsprechung())
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
    ImplicitReference aktivReference = new ImplicitReference();
    aktivReference.setReferenzRechtsprechung(aktiv);
    var passiv = new ReferenzRechtsprechung();
    passiv.setRichtung("passiv");
    passiv.setDokumentnummer("JURE100074208");
    ImplicitReference passivReference = new ImplicitReference();
    passivReference.setReferenzRechtsprechung(passiv);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(Arrays.asList(null, aktivReference, passivReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.zitierungenRechtsprechung())
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
    ImplicitReference unknownReference = new ImplicitReference();
    unknownReference.setReferenzRechtsprechung(unknown);
    var missing = new ReferenzRechtsprechung();
    missing.setDokumentnummer("KORE300002025");
    ImplicitReference missingReference = new ImplicitReference();
    missingReference.setReferenzRechtsprechung(missing);
    var valid = new ReferenzRechtsprechung();
    valid.setRichtung("aktiv");
    valid.setDokumentnummer("JURE100074208");
    ImplicitReference validReference = new ImplicitReference();
    validReference.setReferenzRechtsprechung(valid);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(
        List.of(unknownReference, missingReference, validReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.zitierungenRechtsprechung())
        .containsExactly(
            ZitierungRechtsprechung.builder()
                .richtung(Richtung.AKTIV)
                .dokumentnummer("JURE100074208")
                .build());
  }

  @Test
  @DisplayName("Maps a Fundstelle without Periodikum")
  void mapsFundstelleWithoutPeriodikum() {
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelle.setZitatstelle("1999, 641");
    var referenz = new ReferenzUnselbststaendigeLiteratur();
    referenz.setRichtung("passiv");
    referenz.setDokumentnummer("STLU991393280");
    referenz.setFundstelle(fundstelle);
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setReferenzUnselbststaendigeLiteratur(referenz);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(implicitReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.zitierungenLiteraturUnselbststaendig())
        .containsExactly(
            ZitierungLiteraturUnselbststaendig.builder()
                .richtung(Richtung.PASSIV)
                .dokumentnummer("STLU991393280")
                .fundstelle(Fundstelle.builder().zitatstelle("1999, 641").build())
                .build());
  }
}
