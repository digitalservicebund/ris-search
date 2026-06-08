package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.SET;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.ChangelogResponseMapper;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.schema.ChangelogResponse;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ChangelogResponseMapperTest {

  @ParameterizedTest
  @EnumSource(
      value = DocumentKind.class,
      names = {"CASE_LAW", "LITERATURE", "ADMINISTRATIVE_DIRECTIVE"})
  void itIgnoresNonRootDocumentsInDeletedList(DocumentKind documentKind) {
    Changelog changelog =
        new Changelog(
            new HashSet<>(List.of("ABCD00001/ABCD00001.xml")),
            new HashSet<>(List.of("ABCD00001/ABCD00001-attachment.xml", "ABCD00001/ABCD00001.jpg")),
            false);

    ChangelogResponse actual = ChangelogResponseMapper.mapChangelog(changelog, documentKind);

    assertThat(actual.deleted().isEmpty());
    assertThat(actual.changed())
        .asInstanceOf(SET)
        .hasSize(1)
        .extracting("id") // Or use the method reference: ChangelogChangedDocument::id
        .anySatisfy(id -> assertThat(id.toString()).contains("ABCD00001"));
  }

  @Test
  void itIgnoresNonRootDocumentsInDeletedListForLegislation() {
    Changelog changelog =
        new Changelog(
            new HashSet<>(
                List.of(
                    "eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu/2026-01-01/regelungstext-verkuendung-1.xml")),
            new HashSet<>(
                List.of(
                    "eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu/2026-01-01/anlage-1.xml")),
            false);

    ChangelogResponse actual =
        ChangelogResponseMapper.mapChangelog(changelog, DocumentKind.LEGISLATION);

    // Should ignore the anlage and non-root files, leaving deleted empty
    assertThat(actual.deleted()).asInstanceOf(SET).isEmpty();
    assertThat(actual.changed())
        .asInstanceOf(SET)
        .hasSize(1)
        .extracting("id")
        .anySatisfy(
            id ->
                assertThat(id.toString())
                    .contains("eli/bund/bgbl-1/1999/identifier/2026-01-01/1/deu"));
  }
}
