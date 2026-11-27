package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.Einzelnorm;
import de.bund.digitalservice.ris.search.models.ldml.Fundstelle;
import de.bund.digitalservice.ris.search.models.ldml.Gesetzeskraft;
import de.bund.digitalservice.ris.search.models.ldml.Periodikum;
import de.bund.digitalservice.ris.search.models.ldml.ReferenzNorm;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImplicitReferenceTest {

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
