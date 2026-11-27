package de.bund.digitalservice.ris.search.models.ldml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReferenzNormTest {

  @Test
  void getFormattedJoinsAbkuerzungBezeichnungAndFassungsdatumOfFirstEinzelnorm() {
    Einzelnorm first = new Einzelnorm();
    first.setBezeichnung("Art 6 Abs 2 S 1");
    first.setFassungsdatum("1949-05-23");
    Einzelnorm second = new Einzelnorm();
    second.setBezeichnung("Art 1");
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setAbkuerzung("GG");
    referenzNorm.setEinzelnorm(List.of(first, second));

    assertThat(referenzNorm.getFormatted()).isEqualTo("GG, Art 6 Abs 2 S 1, 1949-05-23");
  }

  @Test
  void getFormattedLeavesOutBlankParts() {
    Einzelnorm einzelnorm = new Einzelnorm();
    einzelnorm.setBezeichnung("Art 6");
    einzelnorm.setFassungsdatum(" ");
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setAbkuerzung("GG");
    referenzNorm.setEinzelnorm(List.of(einzelnorm));

    assertThat(referenzNorm.getFormatted()).isEqualTo("GG, Art 6");
  }

  @Test
  void getFormattedReturnsAbkuerzungWhenThereIsNoEinzelnorm() {
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setAbkuerzung("GG");

    assertThat(referenzNorm.getFormatted()).isEqualTo("GG");
  }

  @Test
  void getFormattedReturnsNullWhenAbkuerzungIsMissing() {
    Einzelnorm einzelnorm = new Einzelnorm();
    einzelnorm.setBezeichnung("Art 6");
    ReferenzNorm referenzNorm = new ReferenzNorm();
    referenzNorm.setEinzelnorm(List.of(einzelnorm));

    assertThat(referenzNorm.getFormatted()).isNull();
  }
}
