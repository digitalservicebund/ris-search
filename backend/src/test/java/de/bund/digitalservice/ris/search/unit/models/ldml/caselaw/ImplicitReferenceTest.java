package de.bund.digitalservice.ris.search.unit.models.ldml.caselaw;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Einzelnorm;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Fundstelle;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Gesetzeskraft;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Periodikum;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzNorm;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzRechtsprechung;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzSelbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzUnselbststaendigeLiteratur;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ReferenzVerwaltungsvorschrift;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImplicitReferenceTest {

  private static ImplicitReference referenceWithDirection(String richtung) {
    ReferenzRechtsprechung rechtsprechung = new ReferenzRechtsprechung();
    rechtsprechung.setRichtung(richtung);
    rechtsprechung.setDokumentnummer("RS1");

    ReferenzVerwaltungsvorschrift verwaltungsvorschrift = new ReferenzVerwaltungsvorschrift();
    verwaltungsvorschrift.setRichtung(richtung);
    verwaltungsvorschrift.setDokumentnummer("VV1");

    ReferenzSelbststaendigeLiteratur selbststaendigeLiteratur =
        new ReferenzSelbststaendigeLiteratur();
    selbststaendigeLiteratur.setRichtung(richtung);
    selbststaendigeLiteratur.setDokumentnummer("SL1");

    ReferenzUnselbststaendigeLiteratur unselbststaendigeLiteratur =
        new ReferenzUnselbststaendigeLiteratur();
    unselbststaendigeLiteratur.setRichtung(richtung);
    unselbststaendigeLiteratur.setDokumentnummer("UL1");

    ImplicitReference reference = new ImplicitReference();
    reference.setReferenzRechtsprechung(rechtsprechung);
    reference.setReferenzVerwaltungsvorschrift(verwaltungsvorschrift);
    reference.setReferenzSelbststaendigeLiteratur(selbststaendigeLiteratur);
    reference.setReferenzUnselbststaendigeLiteratur(unselbststaendigeLiteratur);
    return reference;
  }

  @Test
  void activeCitationsReturnDocumentNumberOnlyWhenDirectionIsActive() {
    ImplicitReference reference = referenceWithDirection("AKTIV");

    assertThat(reference.getAktivzitierteRechtsprechungDokumentnummer()).isEqualTo("RS1");
    assertThat(reference.getAktivzitierteVerwaltungsvorschriftDokumentnummer()).isEqualTo("VV1");
    assertThat(reference.getAktivzitierteSelbststaendigeLiteraturDokumentnummer()).isEqualTo("SL1");
    assertThat(reference.getAktivzitierteUnselbststaendigeLiteraturDokumentnummer())
        .isEqualTo("UL1");

    assertThat(reference.getPassivzitierteRechtsprechungDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteVerwaltungsvorschriftDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteSelbststaendigeLiteraturDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteUnselbststaendigeLiteraturDokumentnummer()).isNull();
  }

  @Test
  void passiveCitationsReturnDocumentNumberOnlyWhenDirectionIsPassive() {
    ImplicitReference reference = referenceWithDirection("passiv");

    assertThat(reference.getPassivzitierteRechtsprechungDokumentnummer()).isEqualTo("RS1");
    assertThat(reference.getPassivzitierteVerwaltungsvorschriftDokumentnummer()).isEqualTo("VV1");
    assertThat(reference.getPassivzitierteSelbststaendigeLiteraturDokumentnummer())
        .isEqualTo("SL1");
    assertThat(reference.getPassivzitierteUnselbststaendigeLiteraturDokumentnummer())
        .isEqualTo("UL1");

    assertThat(reference.getAktivzitierteRechtsprechungDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteVerwaltungsvorschriftDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteSelbststaendigeLiteraturDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteUnselbststaendigeLiteraturDokumentnummer()).isNull();
  }

  @Test
  void citationGettersReturnNullWhenTheReferenceIsAbsent() {
    ImplicitReference reference = new ImplicitReference();

    assertThat(reference.getAktivzitierteRechtsprechungDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteRechtsprechungDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteVerwaltungsvorschriftDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteVerwaltungsvorschriftDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteSelbststaendigeLiteraturDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteSelbststaendigeLiteraturDokumentnummer()).isNull();
    assertThat(reference.getAktivzitierteUnselbststaendigeLiteraturDokumentnummer()).isNull();
    assertThat(reference.getPassivzitierteUnselbststaendigeLiteraturDokumentnummer()).isNull();
  }

  private static Fundstelle fundstelle(String abkuerzung, String zitatstelle, String typ) {
    Periodikum periodikum = new Periodikum();
    periodikum.setAbkuerzung(abkuerzung);

    Fundstelle fundstelle = new Fundstelle();
    fundstelle.setPeriodikum(periodikum);
    fundstelle.setZitatstelle(zitatstelle);
    fundstelle.setFundstellenTyp(typ);
    return fundstelle;
  }

  @Test
  void getAmtlicheFundstelleFormattedReturnsFormattedCitationOnlyWhenOfficial() {
    ImplicitReference reference = new ImplicitReference();
    reference.setFundstelle(fundstelle("BGHSt", "67, 273-284", "amtlich"));

    assertThat(reference.getAmtlicheFundstelleFormatted()).isEqualTo("BGHSt 67, 273-284");
    assertThat(reference.getNichtamtlicheFundstelleFormatted()).isNull();
  }

  @Test
  void getNichtamtlicheFundstelleFormattedReturnsFormattedCitationOnlyWhenNonOfficial() {
    ImplicitReference reference = new ImplicitReference();
    reference.setFundstelle(fundstelle("DStR", "2023, 1430-1435", "nichtamtlich"));

    assertThat(reference.getNichtamtlicheFundstelleFormatted()).isEqualTo("DStR 2023, 1430-1435");
    assertThat(reference.getAmtlicheFundstelleFormatted()).isNull();
  }

  @Test
  void fundstelleGettersReturnNullWhenNoFundstelleIsPresent() {
    ImplicitReference reference = new ImplicitReference();

    assertThat(reference.getAmtlicheFundstelleFormatted()).isNull();
    assertThat(reference.getNichtamtlicheFundstelleFormatted()).isNull();
  }

  private static Einzelnorm einzelnormWithGesetzeskraft(
      String bezeichnung, String gesetzeskraftTyp, String geltungsbereich) {
    Gesetzeskraft gesetzeskraft = new Gesetzeskraft();
    gesetzeskraft.setGesetzeskraftTyp(gesetzeskraftTyp);
    gesetzeskraft.setGeltungsbereich(geltungsbereich);

    Einzelnorm einzelnorm = new Einzelnorm();
    einzelnorm.setBezeichnung(bezeichnung);
    einzelnorm.setGesetzeskraft(gesetzeskraft);
    return einzelnorm;
  }

  @Test
  void getGesetzeskraftFormattedListFormatsEveryEinzelnormAndSkipsThoseWithoutARuling() {
    Einzelnorm withoutRuling = new Einzelnorm();
    withoutRuling.setBezeichnung("§ 1");

    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setEinzelnorm(
        List.of(
            einzelnormWithGesetzeskraft("§ 2", "vereinbar mit höherrangigem Recht", "Bremen"),
            withoutRuling));

    ImplicitReference reference = new ImplicitReference();
    reference.setReferenzNorm(referenzNorm);

    assertThat(reference.getGesetzeskraftFormattedList())
        .containsExactly("vereinbar mit höherrangigem Recht, Bremen");
  }

  @Test
  void getGesetzeskraftFormattedListIsEmptyWhenThereIsNoReferenzNorm() {
    assertThat(new ImplicitReference().getGesetzeskraftFormattedList()).isEmpty();
  }

  @Test
  void getNormenketteFormattedListCombinesAbbreviationWithEachProvision() {
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setAbkuerzung("BGB");
    Einzelnorm first = new Einzelnorm();
    first.setBezeichnung("§ 823");
    Einzelnorm second = new Einzelnorm();
    second.setBezeichnung("§ 826");
    referenzNorm.setEinzelnorm(List.of(first, second));

    ImplicitReference reference = new ImplicitReference();
    reference.setReferenzNorm(referenzNorm);

    assertThat(reference.getNormenketteFormattedList()).containsExactly("BGB § 823", "BGB § 826");
  }

  @Test
  void getNormenketteFormattedListReturnsTheAbbreviationAloneWhenNoProvisionsAreCited() {
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setAbkuerzung("BGB");

    ImplicitReference reference = new ImplicitReference();
    reference.setReferenzNorm(referenzNorm);

    assertThat(reference.getNormenketteFormattedList()).containsExactly("BGB");
  }

  @Test
  void getNormenketteFormattedListIsEmptyWhenThereIsNeitherAnAbbreviationNorProvisions() {
    ImplicitReference reference = new ImplicitReference();
    reference.setReferenzNorm(new ReferenzNorm());

    assertThat(reference.getNormenketteFormattedList()).isEmpty();
  }

  @Test
  void getNormenketteFormattedListIsEmptyWhenThereIsNoReferenzNorm() {
    assertThat(new ImplicitReference().getNormenketteFormattedList()).isEmpty();
  }
}
