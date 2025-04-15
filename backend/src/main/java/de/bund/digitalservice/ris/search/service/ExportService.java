package de.bund.digitalservice.ris.search.service;

import static de.bund.digitalservice.ris.search.utils.DateUtils.DATE_FORMATTER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

/** Service class for exporting data to CSV. */
@Service
public class ExportService {

  /**
   * Writes a list of {@link CaseLawDocumentationUnit} to a CSV file and sends it as a response.
   *
   * @param filename The name of the file to be downloaded.
   * @param includedFields The list of fields to be included in the CSV.
   * @param list The list of {@link CaseLawDocumentationUnit} to be written to the CSV.
   * @param response The {@link HttpServletResponse} to be used to send the CSV as a response.
   * @throws CustomValidationException If an error occurs while writing the CSV.
   */
  public void writeListAsCsvToResponse(
      String filename,
      List<String> includedFields,
      List<CaseLawDocumentationUnit> list,
      HttpServletResponse response)
      throws CustomValidationException {
    try {
      doWriteListAsCsvToResponse(filename, includedFields, list, response);
    } catch (Exception e) {
      var errors = new ArrayList<CustomError>();
      errors.add(
          CustomError.builder()
              .code("csv_export")
              .parameter("export")
              .message("Error when building CSV file: " + e.getMessage())
              .build());
      throw new CustomValidationException(errors);
    }
  }

  /**
   * Writes a list of {@link CaseLawDocumentationUnit} to a CSV file and sends it as a response.
   *
   * @param filename The name of the file to be downloaded.
   * @param includedFields The list of fields to be included in the CSV.
   * @param list The list of {@link CaseLawDocumentationUnit} to be written to the CSV.
   * @param response The {@link HttpServletResponse} to be used to send the CSV as a response.
   */
  private void doWriteListAsCsvToResponse(
      String filename,
      List<String> includedFields,
      List<CaseLawDocumentationUnit> list,
      HttpServletResponse response)
      throws IOException {
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=" + filename);

    CSVFormat header =
        CSVFormat.DEFAULT.builder().setHeader(includedFields.toArray(new String[0])).get();

    try (CSVPrinter printer = new CSVPrinter(response.getWriter(), header)) {
      ObjectMapper mapper = new ObjectMapper();
      JavaTimeModule javaTimeModule = new JavaTimeModule();
      // use format yyyy-MM-dd to serialize LocalDates
      javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
      mapper.registerModule(javaTimeModule);
      for (CaseLawDocumentationUnit item : list) {
        var itemMap = mapper.convertValue(item, Map.class);
        Object[] values = new Object[includedFields.size()];
        for (int i = 0; i < includedFields.size(); i++) {
          values[i] = itemMap.get(includedFields.get(i));
          // custom-serialize fileNumber list?
        }
        printer.printRecord(values);
      }
    }
  }
}
