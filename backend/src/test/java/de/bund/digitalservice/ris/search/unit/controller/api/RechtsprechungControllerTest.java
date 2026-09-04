package de.bund.digitalservice.ris.search.unit.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.html.service.xslt.CaselawXsltTransformer;
import de.bund.digitalservice.ris.search.controller.api.RechtsprechungController;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.api.parameters.ChangelogParams;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Basic unit-level coverage for the endpoints {@link RechtsprechungController} mirrors from {@code
 * CaseLawController} onto {@code /v1/rechtsprechung/**}. Broader end-to-end verification (that each
 * mirrored endpoint actually matches its case-law counterpart) lives in {@code
 * RechtsprechungMirrorsCaseLawApiTest}.
 */
@ExtendWith(MockitoExtension.class)
class RechtsprechungControllerTest {

  @InjectMocks RechtsprechungController controller;

  @Mock CaseLawService caseLawService;

  @Mock CaselawXsltTransformer caselawXsltTransformer;

  @Mock ChangelogService<CaseLawBucket> changelogService;

  private static final String DOCUMENT_NUMBER = "STRE201770751";

  @Test
  void itReturnsHtml() throws ObjectStoreServiceException {
    byte[] content = "xml content".getBytes();
    when(caseLawService.getFileByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Optional.of(content));
    when(caselawXsltTransformer.transform(eq(content), any())).thenReturn("<html></html>");

    var response = controller.getCaseLawDocumentationUnitAsHtml(DOCUMENT_NUMBER);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo("<html></html>");
  }

  @Test
  void itReturns404WhenHtmlDocumentIsMissing() throws ObjectStoreServiceException {
    when(caseLawService.getFileByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Optional.empty());

    var response = controller.getCaseLawDocumentationUnitAsHtml(DOCUMENT_NUMBER);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void itReturnsXml() throws ObjectStoreServiceException {
    byte[] content = "xml content".getBytes();
    when(caseLawService.getFileByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(Optional.of(content));

    var response = controller.getCaseLawDocumentationUnitAsXml(DOCUMENT_NUMBER);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(content);
  }

  @Test
  void itReturnsZip() {
    when(caseLawService.getAllFilenamesByDocumentNumber(DOCUMENT_NUMBER))
        .thenReturn(List.of(DOCUMENT_NUMBER + "/" + DOCUMENT_NUMBER + ".xml"));

    var response = controller.getCaseLawDocumentationUnitAsZip(DOCUMENT_NUMBER);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
  }

  @Test
  void itReturns404WhenZipHasNoFiles() {
    when(caseLawService.getAllFilenamesByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(List.of());

    var response = controller.getCaseLawDocumentationUnitAsZip(DOCUMENT_NUMBER);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void itReturnsAnImageResource() throws Exception {
    byte[] content = "image bytes".getBytes();
    when(caseLawService.getFileByPath(DOCUMENT_NUMBER + "/image.png"))
        .thenReturn(Optional.of(content));

    var response = controller.getImage(DOCUMENT_NUMBER, "image", "png");

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(content);
  }

  @Test
  void itReturnsChangelogs() {
    ChangelogParams params = new ChangelogParams();
    params.setFrom(OffsetDateTime.parse("2026-07-03T12:00:00Z"));
    params.setTo(OffsetDateTime.parse("2026-07-04T12:00:00Z"));
    when(changelogService.getChangesBetween(any(), any()))
        .thenReturn(new Changelog(new HashSet<>(), new HashSet<>(), false));

    var response = controller.getChangelogs(params);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
  }
}
