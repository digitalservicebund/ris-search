package de.bund.digitalservice.ris.search.integration.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
class LiteratureLdmlToOpensearchMapperIntergrationTest {

  @Test
  void mapLiteratureLdmlToOpensearchIndex() {
    String literatureContent =
        LoadXmlUtils.loadXmlAsString(Literature.class, "literatureLdml-1.akn.xml");

    Literature literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureContent);

    assertThat(literature.id()).isEqualTo("ABCD0000000001");
    assertThat(literature.documentNumber()).isEqualTo("ABCD0000000001");
    assertThat(literature.yearsOfPublication()).containsExactly("2025");
    assertThat(literature.firstPublicationDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(literature.documentTypes()).containsExactly("Auf", "Foo");
    assertThat(literature.dependentReferences()).containsExactly("BB, 1979, 1298-1300");
    assertThat(literature.independentReferences())
        .containsExactly("Titel einer Fundstelle, 1979, 1298-1300");
    assertThat(literature.mainTitle()).isEqualTo("Literatur Test Dokument");
    assertThat(literature.documentaryTitle()).isEqualTo("Dokumentarischer Titel");
    assertThat(literature.authors()).containsExactly("Mustermann, Max", "Musterfrau, Susanne");
    assertThat(literature.collaborators()).containsExactly("Foo, Peter");
    assertThat(literature.conferenceNotes())
        .containsExactly("Internationaler Kongreß für das Recht, 1991, Athen, GRC");
    assertThat(literature.languages()).containsExactly("deu");
    assertThat(literature.normReferences()).containsExactly("GG, Art 6 Abs 2 S 1, 1949-05-23");
    assertThat(literature.shortReport())
        .isEqualTo(
            "1. Dies ist ein literature LDML Dokument für Tests. Es werden sub und sup Elemente unterstützt. Außerdem gib es noch EM, hlj, noindex und strong.");
    assertThat(literature.outline()).isEqualTo("I. Äpfel. II. Birnen. III. Orangen.");
  }
}
