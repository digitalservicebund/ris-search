package de.bund.digitalservice.ris.search.unit.models.ldml.caselaw;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknDissentingOpinion;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknMotivationBlock;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.AknOpinion;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisMeta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.RisPerson;
import java.util.List;
import org.junit.jupiter.api.Test;

class AknDissentingOpinionTest {

  @Test
  void returnsEmptyStringWhenNothingIsSet() {
    AknDissentingOpinion opinion = AknDissentingOpinion.builder().build();

    assertThat(opinion.toFormattedText(null)).isEmpty();
  }

  @Test
  void joinsMultipleParagraphsWithASpace() {
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .paragraphs(List.of("First sentence.", "Second sentence."))
            .build();

    assertThat(opinion.toFormattedText(null)).isEqualTo("First sentence. Second sentence.");
  }

  @Test
  void resolvesOpinionAuthorNameFromRisMeta() {
    RisMeta risMeta =
        RisMeta.builder()
            .risPersonen(
                List.of(RisPerson.builder().eId("richter-1").showAs("Dr. Max Mustermann").build()))
            .build();
    AknOpinion opinionEntry = AknOpinion.builder().by("#richter-1").text("dissenting view").build();
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .aknMotivationBlock(
                AknMotivationBlock.builder().opinions(List.of(opinionEntry)).build())
            .build();

    assertThat(opinion.toFormattedText(risMeta)).isEqualTo("Dr. Max Mustermann: dissenting view");
  }

  @Test
  void fallsBackToTheEidWhenPersonIsNotFoundInRisMeta() {
    RisMeta risMeta = RisMeta.builder().risPersonen(List.of()).build();
    AknOpinion opinionEntry = AknOpinion.builder().by("#unknown-person").text("some view").build();
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .aknMotivationBlock(
                AknMotivationBlock.builder().opinions(List.of(opinionEntry)).build())
            .build();

    assertThat(opinion.toFormattedText(risMeta)).isEqualTo("unknown-person: some view");
  }

  @Test
  void fallsBackToTheEidWhenRisMetaIsNull() {
    AknOpinion opinionEntry = AknOpinion.builder().by("#richter-1").text("some view").build();
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .aknMotivationBlock(
                AknMotivationBlock.builder().opinions(List.of(opinionEntry)).build())
            .build();

    assertThat(opinion.toFormattedText(null)).isEqualTo("richter-1: some view");
  }

  @Test
  void combinesParagraphsAndOpinionsIntoACommaSeparatedList() {
    RisMeta risMeta =
        RisMeta.builder()
            .risPersonen(
                List.of(RisPerson.builder().eId("richter-1").showAs("Maxima Mustermann").build()))
            .build();
    AknOpinion opinionEntry = AknOpinion.builder().by("#richter-1").text("first opinion").build();
    AknOpinion secondOpinionEntry =
        AknOpinion.builder().by("#richter-2").text("second opinion").build();
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .paragraphs(List.of("dissenting test"))
            .aknMotivationBlock(
                AknMotivationBlock.builder()
                    .opinions(List.of(opinionEntry, secondOpinionEntry))
                    .build())
            .build();

    assertThat(opinion.toFormattedText(risMeta))
        .isEqualTo("dissenting test, Maxima Mustermann: first opinion, richter-2: second opinion");
  }

  @Test
  void treatsAMissingOpinionTextAsEmpty() {
    AknOpinion opinionEntry = AknOpinion.builder().by("#richter-1").build();
    AknDissentingOpinion opinion =
        AknDissentingOpinion.builder()
            .aknMotivationBlock(
                AknMotivationBlock.builder().opinions(List.of(opinionEntry)).build())
            .build();

    assertThat(opinion.toFormattedText(null)).isEqualTo("richter-1: ");
  }
}
