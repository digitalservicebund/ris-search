package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.caselaw.Analysis;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.ImplicitReference;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.Meta;
import de.bund.digitalservice.ris.search.models.ldml.caselaw.OtherReferences;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawUnindexed;
import de.bund.digitalservice.ris.search.models.opensearch.NachgehendeEntscheidung;
import de.bund.digitalservice.ris.search.models.opensearch.VorgehendeEntscheidung;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawUnindexedMapperTest {

  @Test
  @DisplayName("Returns empty lists when the document has no analysis")
  void returnsEmptyListsWithoutAnalysis() {
    CaseLawUnindexed unindexed = CaseLawUnindexedMapper.map(Meta.builder().build());

    assertThat(unindexed.vorgehendeEntscheidungen()).isEmpty();
    assertThat(unindexed.nachgehendeEntscheidungen()).isEmpty();
  }

  @Test
  @DisplayName("Maps vorgehende and nachgehende Entscheidungen without Gericht")
  void mapsEntscheidungenWithoutGericht() {
    var vorgehende =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.VorgehendeEntscheidung();
    vorgehende.setDokumentnummer("KORE300002025");
    ImplicitReference vorgehendeReference = new ImplicitReference();
    vorgehendeReference.setVorgehendeEntscheidung(vorgehende);
    var nachgehende =
        new de.bund.digitalservice.ris.search.models.ldml.caselaw.NachgehendeEntscheidung();
    nachgehende.setDokumentnummer("JURE100074208");
    ImplicitReference nachgehendeReference = new ImplicitReference();
    nachgehendeReference.setNachgehendeEntscheidung(nachgehende);
    OtherReferences otherReferences = new OtherReferences();
    otherReferences.setImplicitReferences(List.of(vorgehendeReference, nachgehendeReference));
    Analysis analysis = new Analysis();
    analysis.setOtherReferences(otherReferences);

    CaseLawUnindexed unindexed =
        CaseLawUnindexedMapper.map(Meta.builder().analysis(analysis).build());

    assertThat(unindexed.vorgehendeEntscheidungen())
        .containsExactly(VorgehendeEntscheidung.builder().dokumentnummer("KORE300002025").build());
    assertThat(unindexed.nachgehendeEntscheidungen())
        .containsExactly(NachgehendeEntscheidung.builder().dokumentnummer("JURE100074208").build());
  }
}
