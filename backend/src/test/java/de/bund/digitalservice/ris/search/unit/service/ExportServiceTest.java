package de.bund.digitalservice.ris.search.unit.service;

import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

  private final ExportService exportService = new ExportService();

  @Mock private HttpServletResponse response;

  private StringWriter responseWriter;

  private final List<CaseLawDocumentationUnit> list =
      List.of(
          CaseLawDocumentationUnit.builder()
              .documentNumber("Document1234")
              .courtType("Some court type")
              .location("Berlin")
              .decisionDate(LocalDate.of(2024, 1, 1))
              .fileNumbers(List.of("File 555"))
              .documentType("default")
              .build());

  @BeforeEach
  void setUp() throws IOException {
    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  void shouldExportCaseLawAsCsvWithAllFieldsCorrectly() throws Exception {
    List<String> includedFields =
        List.of(
            "documentNumber",
            "fileNumbers",
            "courtType",
            "location",
            "decisionDate",
            "documentType");
    String filename = "test.csv";

    exportService.writeListAsCsvToResponse(filename, includedFields, list, response);

    verify(response).setContentType("text/csv");
    verify(response).setHeader(eq("Content-Disposition"), contains(filename));

    String actualCsvContent =
        responseWriter.toString().replace("\r\n", "\n"); // normalize to LF line endings
    String expectedCsvContent =
        """
                    documentNumber,fileNumbers,courtType,location,decisionDate,documentType
                    Document1234,[File 555],Some court type,Berlin,2024-01-01,default
                    """;

    Assertions.assertEquals(expectedCsvContent, actualCsvContent);
  }

  @Test
  void shouldExportCaseLawAsCsvWithSomeFieldsInDifferentOrderCorrectly() throws Exception {
    List<String> includedFields = List.of("courtType", "decisionDate", "location");

    exportService.writeListAsCsvToResponse("test.csv", includedFields, list, response);

    String actualCsvContent = responseWriter.toString().replace("\r\n", "\n");
    String expectedCsvContent =
        """
                        courtType,decisionDate,location
                        Some court type,2024-01-01,Berlin
                        """;

    Assertions.assertEquals(expectedCsvContent, actualCsvContent);
  }
}
