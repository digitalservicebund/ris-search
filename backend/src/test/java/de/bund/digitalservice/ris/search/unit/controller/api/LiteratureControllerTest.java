package de.bund.digitalservice.ris.search.unit.controller.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.controller.api.LiteratureController;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import de.bund.digitalservice.ris.search.service.xslt.LiteratureXsltTransformerService;
import de.bund.digitalservice.ris.search.service.xslt.SliLiteratureXsltTransformerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LiteratureControllerTest {

  LiteratureController controller;

  @Mock LiteratureService literatureService;

  @Mock LiteratureXsltTransformerService uliTransformer;

  @Mock SliLiteratureXsltTransformerService sliTransformer;

  @Mock LiteratureBucket bucket;

  @BeforeEach
  void setup() {
    controller = new LiteratureController(literatureService, uliTransformer, sliTransformer);
  }

  @Test
  void itCallsTheUliTransformer() throws ObjectStoreServiceException {

    byte[] content = "content".getBytes();
    String documentNumber = "XXLU00000";

    when(literatureService.getFileByDocumentNumber(documentNumber))
        .thenReturn(Optional.of(content));
    when(uliTransformer.transformLiterature(content)).thenReturn("uliHtml");

    var actual = controller.getLiteratureAsHtml(documentNumber);

    Mockito.verify(sliTransformer, never()).transformLiterature(any());
    assertThat(actual.getBody()).isEqualTo("uliHtml");
  }

  @Test
  void itCallsTheSliTransformer() throws ObjectStoreServiceException {

    byte[] content = "content".getBytes();
    String documentNumber = "XXLS00000";

    when(literatureService.getFileByDocumentNumber(documentNumber))
        .thenReturn(Optional.of(content));
    when(sliTransformer.transformLiterature(content)).thenReturn("sliHtml");

    var actual = controller.getLiteratureAsHtml(documentNumber);

    Mockito.verify(uliTransformer, never()).transformLiterature(any());
    assertThat(actual.getBody()).isEqualTo("sliHtml");
  }
}
