package de.bund.digitalservice.ris.search.integration.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

@Tag("integration")
class LiteratureLdmlToOpensearchMapperIntergrationTest {

  @Test
  void mapLiteratureLdmlToOpensearchIndex() throws IOException {
    File file = ResourceUtils.getFile("classpath:data/LDML/literature/literatureLdml-1.akn.xml");
    String literatureContent = new String(Files.readAllBytes(file.toPath()));

    Optional<Literature> literature = LiteratureLdmlToOpenSearchMapper.mapLdml(literatureContent);
    assertThat(literature).isPresent();
    var literatureUnwrapped = literature.get();
    assertThat(literatureUnwrapped.id()).isEqualTo("ABCD0000000001");
    assertThat(literatureUnwrapped.documentNumber()).isEqualTo("ABCD0000000001");
    assertThat(literatureUnwrapped.yearsOfPublication()).containsExactly("2025");
    assertThat(literatureUnwrapped.firstPublicationDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(literatureUnwrapped.documentTypes()).containsExactly("Auf", "Foo");
    assertThat(literatureUnwrapped.dependentReferences()).containsExactly("BB 1979, 1298-1300");
    assertThat(literatureUnwrapped.independentReferences())
        .containsExactly("Titel einer Fundstelle 1979, 1298-1300");
    assertThat(literatureUnwrapped.mainTitle()).isEqualTo("Literatur Test Dokument");
    assertThat(literatureUnwrapped.mainTitleAdditions()).isEqualTo("Titelzusatz");
    assertThat(literatureUnwrapped.documentaryTitle()).isEqualTo("Dokumentarischer Titel");
    assertThat(literatureUnwrapped.authors())
        .containsExactly("Mustermann, Max", "Musterfrau, Susanne");
    assertThat(literatureUnwrapped.collaborators()).containsExactly("Foo, Peter");
    assertThat(literatureUnwrapped.originators()).containsExactly("FOO");
    assertThat(literatureUnwrapped.conferenceNotes())
        .containsExactly("Internationaler Kongreß für das Recht, 1991, Athen, GRC");
    assertThat(literatureUnwrapped.languages()).containsExactly("deu");
    assertThat(literatureUnwrapped.normReferences())
        .containsExactly("GG, Art 6 Abs 2 S 1, 1949-05-23");
    assertThat(literatureUnwrapped.shortReport())
        .isEqualTo(
            "1. Dies ist ein literature LDML Dokument für Tests. Es werden sub und sup Elemente unterstützt. Außerdem gib es noch EM, hlj, noindex und strong.");
    assertThat(literatureUnwrapped.outline()).isEqualTo("I. Äpfel. II. Birnen. III. Orangen.");
  }
}
