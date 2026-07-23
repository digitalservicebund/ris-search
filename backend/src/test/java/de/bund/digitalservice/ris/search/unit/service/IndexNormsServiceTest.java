package de.bund.digitalservice.ris.search.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.ArticlesRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class IndexNormsServiceTest {

  @InjectMocks IndexNormsService service;

  @Mock NormsBucket bucket;
  @Mock Environment environment;
  @Mock NormsRepository repo;
  @Mock ArticlesRepository articlesRepository;

  @Test
  void reindexAllIgnoresInvalidFiles() throws ObjectStoreServiceException {
    String validEli = "eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu/1992-01-02/regelungstext-1.xml";
    when(this.bucket.getAllKeysByPrefix("eli/")).thenReturn(List.of(validEli, "eli/not_an_eli"));
    when(this.bucket.getFileAsString(validEli))
        .thenReturn(Optional.of(NormTestDataBuilder.builder().eli(validEli).buildNormXml()));

    String startingTimestamp = "2024-01-01T12:00:00Z";
    this.service.reindexAll(startingTimestamp);

    verify(repo, times(1)).saveAll(any());
    verify(repo, times(1))
        .saveAll(
            argThat(
                arg ->
                    arg.iterator()
                        .next()
                        .getId()
                        .equals("eli/bund/bgbl-1/1992/s101/1992-01-01/1/deu")));
    verify(repo, times(1)).deleteByIndexedAtBefore(startingTimestamp);
  }

  @Test
  void itReturnsRightNumberOfFiles() {
    when(this.bucket.getAllKeysByPrefix("eli/"))
        .thenReturn(
            List.of(
                "eli/bund/bgbl-1/2013/s323/2018-07-02/2/deu/2025-03-08/regelungstext-1.xml",
                "eli/bund/bgbl-1/2013/s4098/2022-03-15/2/deu/2025-03-08/regelungstext-1.xml",
                "eli/bund/bgbl-1/2013/s1925/2015-10-12/2/deu/2025-03-08/offenestruktur-1.xml"));
    assertThat(service.getNumberOfIndexableDocumentsInBucket()).isEqualTo(2);
  }
}
