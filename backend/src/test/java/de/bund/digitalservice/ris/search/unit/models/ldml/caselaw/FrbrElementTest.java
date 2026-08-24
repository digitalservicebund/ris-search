package de.bund.digitalservice.ris.search.unit.models.ldml.caselaw;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.FrbrAlias;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.FrbrDate;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.FrbrElement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class FrbrElementTest {

  private static FrbrAlias alias(String name, String value) {
    FrbrAlias alias = new FrbrAlias();
    ReflectionTestUtils.setField(alias, "name", name);
    ReflectionTestUtils.setField(alias, "value", value);
    return alias;
  }

  private static FrbrDate date(String name, String value) {
    FrbrDate date = new FrbrDate();
    ReflectionTestUtils.setField(date, "name", name);
    ReflectionTestUtils.setField(date, "date", value);
    return date;
  }

  @Test
  void resolvesAliasValuesByNameCaseInsensitively() {
    FrbrElement frbrElement =
        FrbrElement.builder()
            .frbrAlias(
                List.of(
                    alias("ECLI", "ECLI:DE:BVerfG:2020:rs20200101"),
                    alias("Aktenzeichen", "1 BvR 123/20"),
                    alias("CELEX", "62020CJ0001")))
            .build();

    assertThat(frbrElement.getEcliAliasValue()).isEqualTo("ECLI:DE:BVerfG:2020:rs20200101");
    assertThat(frbrElement.getAktenzeichenAliasValue()).isEqualTo("1 BvR 123/20");
    assertThat(frbrElement.getCelexAliasValue()).isEqualTo("62020CJ0001");
  }

  @Test
  void returnsNullWhenNoAliasMatchesTheRequestedName() {
    FrbrElement frbrElement =
        FrbrElement.builder().frbrAlias(List.of(alias("Aktenzeichen", "1 BvR 123/20"))).build();

    assertThat(frbrElement.getCelexAliasValue()).isNull();
  }

  @Test
  void returnsNullFromAliasLookupsWhenNoAliasesArePresent() {
    FrbrElement frbrElement = FrbrElement.builder().build();

    assertThat(frbrElement.getEcliAliasValue()).isNull();
  }

  @Test
  void getEntscheidungsdatumValuePrefersTheEntscheidungsdatumEvenWhenItIsNotFirst() {
    FrbrElement frbrElement =
        FrbrElement.builder()
            .frbrDates(
                List.of(date("Other date", "2024-05-10"), date("Entscheidungsdatum", "2024-05-01")))
            .build();

    assertThat(frbrElement.getEntscheidungsdatumValue()).isEqualTo("2024-05-01");
  }

  @Test
  void getEntscheidungsdatumValueFallsBackToMitteilungsdatumWhenNoneIsNamedEntscheidungsdatum() {
    FrbrElement frbrElement =
        FrbrElement.builder().frbrDates(List.of(date("mitteilungsdatum", "2024-05-10"))).build();

    assertThat(frbrElement.getEntscheidungsdatumValue()).isEqualTo("2024-05-10");
  }

  @Test
  void
      getEntscheidungsdatumValueReturnsNullWhenNeitherEntscheidungsdatumNorMitteilungsdatumIsPresent() {
    FrbrElement frbrElement =
        FrbrElement.builder().frbrDates(List.of(date("other date", "2024-05-10"))).build();

    assertThat(frbrElement.getEntscheidungsdatumValue()).isNull();
  }

  @Test
  void getEntscheidungsdatumValueReturnsNullWhenNoDatesArePresent() {
    FrbrElement frbrElement = FrbrElement.builder().build();

    assertThat(frbrElement.getEntscheidungsdatumValue()).isNull();
  }

  @Test
  void getDateByNameLooksUpADateValueCaseInsensitively() {
    FrbrElement frbrElement =
        FrbrElement.builder()
            .frbrDates(List.of(date("erstveroeffentlichung", "2024-05-09")))
            .build();

    assertThat(frbrElement.getErstveroeffentlichungValue()).isEqualTo("2024-05-09");
    assertThat(frbrElement.getLetzteVeroeffentlichungValue()).isNull();
    assertThat(frbrElement.getMitteilungsdatumValue()).isNull();
  }
}
