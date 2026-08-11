package de.bund.digitalservice.ris.search.unit.controller.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.html.service.xslt.LiteratureXsltTransformer;
import de.bund.digitalservice.ris.html.service.xslt.SliLiteratureXsltTransformer;
import de.bund.digitalservice.ris.search.controller.api.LiteratureController;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.service.LiteratureService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LiteratureControllerTest {

  @InjectMocks LiteratureController controller;

  @Mock LiteratureService literatureService;

  @Mock LiteratureXsltTransformer uliTransformer;

  @Mock SliLiteratureXsltTransformer sliTransformer;

  @Test
  void itCallsTheUliTransformer() throws ObjectStoreServiceException {

    byte[] content = "content".getBytes();
    String documentNumber = "XXLU00000";

    when(literatureService.getFileByDocumentNumber(documentNumber))
        .thenReturn(Optional.of(content));
    when(uliTransformer.transform(content)).thenReturn("uliHtml");

    var actual = controller.getLiteratureAsHtml(documentNumber);

    verify(sliTransformer, never()).transform(any());
    assertThat(actual.getBody()).isEqualTo("uliHtml");
  }

  @Test
  void itCallsTheSliTransformer() throws ObjectStoreServiceException {

    byte[] content = "content".getBytes();
    String documentNumber = "XXLS00000";

    when(literatureService.getFileByDocumentNumber(documentNumber))
        .thenReturn(Optional.of(content));
    when(sliTransformer.transform(content)).thenReturn("sliHtml");

    var actual = controller.getLiteratureAsHtml(documentNumber);

    verify(uliTransformer, never()).transform(any());
    assertThat(actual.getBody()).isEqualTo("sliHtml");
  }

  @Test
  void itReturns404OnUnknownLiteratureType() throws ObjectStoreServiceException {

    String documentNumber = "XXAB00000";
    var actual = controller.getLiteratureAsHtml(documentNumber);

    assertThat(actual.getStatusCode().value()).isEqualTo(404);
  }
}
