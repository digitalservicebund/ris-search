package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzNorm;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzUnselbststaendigeLiteratur;
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

    assertThat(zitierungen.aktivzitierungRechtsprechung()).isEmpty();
    assertThat(zitierungen.passivzitierungRechtsprechung()).isEmpty();
    assertThat(zitierungen.aktivzitierungLiteraturSelbstaendig()).isEmpty();
    assertThat(zitierungen.passivzitierungLiteraturSelbstaendig()).isEmpty();
    assertThat(zitierungen.aktivzitierungLiteraturUnselbstaendig()).isEmpty();
    assertThat(zitierungen.passivzitierungLiteraturUnselbstaendig()).isEmpty();
    assertThat(zitierungen.aktivzitierungVerwaltungsvorschriften()).isEmpty();
    assertThat(zitierungen.passivzitierungVerwaltungsvorschriften()).isEmpty();
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

    assertThat(zitierungen.aktivzitierungRechtsprechung()).isEmpty();
    assertThat(zitierungen.passivzitierungRechtsprechung()).isEmpty();
  }

  @Test
  @DisplayName("Maps a Rechtsprechung citation without court, date and Fundstelle")
  void mapsPassivzitierungRechtsprechungWithMissingOptionalFields() {
    var referenz =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung();
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

    assertThat(zitierungen.passivzitierungRechtsprechung())
        .containsExactly(ReferenzRechtsprechung.builder().dokumentnummer("JURE100074208").build());
    assertThat(zitierungen.aktivzitierungRechtsprechung()).isEmpty();
  }

  @Test
  @DisplayName("Matches the Richtung case-insensitively and skips unknown Richtungen and nulls")
  void filtersByRichtung() {
    var upperCase =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung();
    upperCase.setRichtung("AKTIV");
    upperCase.setDokumentnummer("BVRE100338409");
    ImplicitReference upperCaseReference = new ImplicitReference();
    upperCaseReference.setReferenzRechtsprechung(upperCase);
    var unknown =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung();
    unknown.setRichtung("unbekannt");
    unknown.setDokumentnummer("UNKNOWN000001");
    ImplicitReference unknownReference = new ImplicitReference();
    unknownReference.setReferenzRechtsprechung(unknown);
    var withoutRichtung =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung();
    withoutRichtung.setDokumentnummer("UNKNOWN000002");
    ImplicitReference withoutRichtungReference = new ImplicitReference();
    withoutRichtungReference.setReferenzRechtsprechung(withoutRichtung);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(
        Arrays.asList(null, upperCaseReference, unknownReference, withoutRichtungReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.aktivzitierungRechtsprechung())
        .containsExactly(ReferenzRechtsprechung.builder().dokumentnummer("BVRE100338409").build());
    assertThat(zitierungen.passivzitierungRechtsprechung()).isEmpty();
  }

  @Test
  @DisplayName("Maps a Fundstelle without Periodikum")
  void mapsFundstelleWithoutPeriodikum() {
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelle.setZitatstelle("1999, 641");
    var referenz =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw
            .ReferenzUnselbststaendigeLiteratur();
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

    assertThat(zitierungen.passivzitierungLiteraturUnselbstaendig())
        .containsExactly(
            ReferenzUnselbststaendigeLiteratur.builder()
                .dokumentnummer("STLU991393280")
                .fundstelle(Fundstelle.builder().zitatstelle("1999, 641").build())
                .build());
  }
}
