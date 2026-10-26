package de.bund.digitalservice.ris.search.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.repository.opensearch.EcliCrawlerDocumentRepository;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EcliCrawlerDocumentRepositoryIntegrationTest extends ContainersIntegrationBase {

  @Autowired private EcliCrawlerDocumentRepository ecliCrawlerDocumentRepository;

  @BeforeEach
  void setUp() {
    ecliCrawlerDocumentRepository.deleteAll();
  }

  @Test
  void findByFilenameReturnsAllDocumentsForMoreFilenamesThanMaxClauseCount() {
    List<String> filenames =
        IntStream.range(0, 2500).mapToObj(i -> "doc" + i + "/doc" + i + ".xml").toList();
    ecliCrawlerDocumentRepository.saveAll(filenames.stream().map(this::mockDocument).toList());

    List<EcliCrawlerDocument> result =
        ecliCrawlerDocumentRepository.findByFilename(filenames).toList();

    assertThat(result)
        .extracting(EcliCrawlerDocument::filename)
        .containsExactlyInAnyOrderElementsOf(filenames);
  }

  @Test
  void findByFilenameReturnsOnlyMatchingDocuments() {
    ecliCrawlerDocumentRepository.saveAll(
        List.of(mockDocument("a/a.xml"), mockDocument("b/b.xml"), mockDocument("c/c.xml")));

    List<EcliCrawlerDocument> result =
        ecliCrawlerDocumentRepository.findByFilename(List.of("a/a.xml", "b/b.xml")).toList();

    assertThat(result)
        .extracting(EcliCrawlerDocument::filename)
        .containsExactlyInAnyOrder("a/a.xml", "b/b.xml");
  }

  @Test
  void findByFilenameReturnsEmptyStreamForEmptyList() {
    ecliCrawlerDocumentRepository.save(mockDocument("a/a.xml"));

    List<EcliCrawlerDocument> result =
        ecliCrawlerDocumentRepository.findByFilename(List.of()).toList();

    assertThat(result).isEmpty();
  }

  private EcliCrawlerDocument mockDocument(String filename) {
    return EcliCrawlerDocument.builder().documentNumber(filename).filename(filename).build();
  }
}
