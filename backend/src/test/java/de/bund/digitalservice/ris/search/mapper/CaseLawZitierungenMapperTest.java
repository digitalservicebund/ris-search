package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Periodikum;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzNorm;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisGericht;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Spruchkoerper;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Fundstelle;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Gericht;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.ReferenzVerwaltungsvorschrift;
import de.bund.digitalservice.ris.search.models.opensearch.zitierungen.Zitierungen;
import java.time.LocalDate;
import java.time.Month;
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
  @DisplayName("Maps a Rechtsprechung citation with all fields into the aktiv list")
  void mapsAktivzitierungRechtsprechung() {
    Periodikum periodikum = new Periodikum();
    periodikum.setAbkuerzung("BayVBl");
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelle.setPeriodikum(periodikum);
    fundstelle.setZitatstelle("1984, 15");
    fundstelle.setFundstellenTyp("amtlich");
    var referenz =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung();
    referenz.setRichtung("aktiv");
    referenz.setReferenzUri("ris:/rechtsprechung/BVRE100338409");
    referenz.setDokumentnummer("BVRE100338409");
    referenz.setArtDerZitierung("Vergleiche");
    referenz.setGericht(
        RisGericht.builder()
            .gerichtstyp("VerfGH")
            .gerichtsort("München")
            .gerichtsbarkeit("Verfassungsgerichtsbarkeit")
            .spruchkoerper(Spruchkoerper.builder().value("1. Senat").build())
            .build());
    referenz.setEntscheidungsdatum("1983-12-02");
    referenz.setDokumenttyp("Beschluss");
    referenz.setAktenzeichen("Vf. 94-VI-81");
    referenz.setFundstelle(fundstelle);
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setReferenzRechtsprechung(referenz);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(implicitReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.aktivzitierungRechtsprechung())
        .containsExactly(
            ReferenzRechtsprechung.builder()
                .referenzUri("ris:/rechtsprechung/BVRE100338409")
                .dokumentnummer("BVRE100338409")
                .artDerZitierung("Vergleiche")
                .gericht(
                    Gericht.builder()
                        .gerichtstyp("VerfGH")
                        .gerichtsort("München")
                        .gerichtsbarkeit("Verfassungsgerichtsbarkeit")
                        .spruchkoerper("1. Senat")
                        .build())
                .entscheidungsdatum(LocalDate.of(1983, Month.DECEMBER, 2))
                .dokumenttyp("Beschluss")
                .aktenzeichen("Vf. 94-VI-81")
                .fundstelle(
                    Fundstelle.builder()
                        .periodikum("BayVBl")
                        .zitatstelle("1984, 15")
                        .fundstellenTyp("amtlich")
                        .build())
                .build());
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
  @DisplayName("Maps selbstständige Literatur citations into aktiv and passiv lists")
  void mapsLiteraturSelbststaendig() {
    var aktiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw
            .ReferenzSelbststaendigeLiteratur();
    aktiv.setRichtung("aktiv");
    aktiv.setReferenzUri("ris:/literatur/KSLS071671727");
    aktiv.setDokumentnummer("KSLS071671727");
    aktiv.setAutor("Arnauld, Andreas von");
    aktiv.setTitel("Die Freiheitsrechte und ihre Schranken");
    aktiv.setVeroeffentlichungsjahr("1999");
    ImplicitReference aktivReference = new ImplicitReference();
    aktivReference.setReferenzSelbststaendigeLiteratur(aktiv);
    var passiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw
            .ReferenzSelbststaendigeLiteratur();
    passiv.setRichtung("passiv");
    passiv.setDokumentnummer("KSLS071671728");
    ImplicitReference passivReference = new ImplicitReference();
    passivReference.setReferenzSelbststaendigeLiteratur(passiv);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(aktivReference, passivReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.aktivzitierungLiteraturSelbstaendig())
        .containsExactly(
            ReferenzSelbststaendigeLiteratur.builder()
                .referenzUri("ris:/literatur/KSLS071671727")
                .dokumentnummer("KSLS071671727")
                .autor("Arnauld, Andreas von")
                .titel("Die Freiheitsrechte und ihre Schranken")
                .veroeffentlichungsjahr("1999")
                .build());
    assertThat(zitierungen.passivzitierungLiteraturSelbstaendig())
        .containsExactly(
            ReferenzSelbststaendigeLiteratur.builder().dokumentnummer("KSLS071671728").build());
  }

  @Test
  @DisplayName("Maps unselbstständige Literatur citations with a Fundstelle without Periodikum")
  void mapsLiteraturUnselbststaendig() {
    Periodikum periodikum = new Periodikum();
    periodikum.setAbkuerzung("NVwZ");
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelle.setPeriodikum(periodikum);
    fundstelle.setZitatstelle("2016, 1139-1141");
    var aktiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw
            .ReferenzUnselbststaendigeLiteratur();
    aktiv.setRichtung("aktiv");
    aktiv.setReferenzUri("ris:/literatur/SBLU000539216");
    aktiv.setDokumentnummer("SBLU000539216");
    aktiv.setAutor("Knopp, Lothar");
    aktiv.setDokumenttyp("Aufsatz");
    aktiv.setFundstelle(fundstelle);
    ImplicitReference aktivReference = new ImplicitReference();
    aktivReference.setReferenzUnselbststaendigeLiteratur(aktiv);
    var fundstelleWithoutPeriodikum =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelleWithoutPeriodikum.setZitatstelle("1999, 641");
    var passiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw
            .ReferenzUnselbststaendigeLiteratur();
    passiv.setRichtung("passiv");
    passiv.setDokumentnummer("STLU991393280");
    passiv.setFundstelle(fundstelleWithoutPeriodikum);
    ImplicitReference passivReference = new ImplicitReference();
    passivReference.setReferenzUnselbststaendigeLiteratur(passiv);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(aktivReference, passivReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.aktivzitierungLiteraturUnselbstaendig())
        .containsExactly(
            ReferenzUnselbststaendigeLiteratur.builder()
                .referenzUri("ris:/literatur/SBLU000539216")
                .dokumentnummer("SBLU000539216")
                .autor("Knopp, Lothar")
                .dokumenttyp("Aufsatz")
                .fundstelle(
                    Fundstelle.builder().periodikum("NVwZ").zitatstelle("2016, 1139-1141").build())
                .build());
    assertThat(zitierungen.passivzitierungLiteraturUnselbstaendig())
        .containsExactly(
            ReferenzUnselbststaendigeLiteratur.builder()
                .dokumentnummer("STLU991393280")
                .fundstelle(Fundstelle.builder().zitatstelle("1999, 641").build())
                .build());
  }

  @Test
  @DisplayName("Maps Verwaltungsvorschrift citations into aktiv and passiv lists")
  void mapsVerwaltungsvorschriften() {
    Periodikum periodikum = new Periodikum();
    periodikum.setAbkuerzung("MittLVA Oberfr");
    var fundstelle = new de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle();
    fundstelle.setPeriodikum(periodikum);
    fundstelle.setZitatstelle("1997, 538-539");
    var aktiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzVerwaltungsvorschrift();
    aktiv.setRichtung("aktiv");
    aktiv.setReferenzUri("ris:/verwaltungsvorschrift/KSNR008561615");
    aktiv.setDokumentnummer("KSNR008561615");
    aktiv.setArtDerZitierung("Vergleiche");
    aktiv.setJurisAbkuerzung("VR VDR 1997-12-04 5883");
    aktiv.setFundstelle(fundstelle);
    ImplicitReference aktivReference = new ImplicitReference();
    aktivReference.setReferenzVerwaltungsvorschrift(aktiv);
    var passiv =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzVerwaltungsvorschrift();
    passiv.setRichtung("passiv");
    passiv.setDokumentnummer("KSNR006800006");
    passiv.setArtDerZitierung("Anwendung");
    ImplicitReference passivReference = new ImplicitReference();
    passivReference.setReferenzVerwaltungsvorschrift(passiv);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(aktivReference, passivReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    Zitierungen zitierungen =
        CaseLawZitierungenMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(zitierungen.aktivzitierungVerwaltungsvorschriften())
        .containsExactly(
            ReferenzVerwaltungsvorschrift.builder()
                .referenzUri("ris:/verwaltungsvorschrift/KSNR008561615")
                .dokumentnummer("KSNR008561615")
                .artDerZitierung("Vergleiche")
                .jurisAbkuerzung("VR VDR 1997-12-04 5883")
                .fundstelle(
                    Fundstelle.builder()
                        .periodikum("MittLVA Oberfr")
                        .zitatstelle("1997, 538-539")
                        .build())
                .build());
    assertThat(zitierungen.passivzitierungVerwaltungsvorschriften())
        .containsExactly(
            ReferenzVerwaltungsvorschrift.builder()
                .dokumentnummer("KSNR006800006")
                .artDerZitierung("Anwendung")
                .build());
  }
}
