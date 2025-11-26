package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A custom XML adapter for handling the conversion between {@code LocalDate} and {@code String}
 * during XML serialization and deserialization.
 *
 * <p>This adapter formats and parses {@code LocalDate} objects using the ISO-8601 date format
 * (e.g., {@code yyyy-MM-dd}). It is useful for mapping {@code LocalDate} fields in JAXB (Jakarta
 * XML Binding) annotated classes to their string representations in XML and vice versa.
 *
 * <p>Methods: - {@code marshal(LocalDate date)}: Converts a {@code LocalDate} object to its
 * ISO-8601 string representation. Returns {@code null} if the input is {@code null}. - {@code
 * unmarshal(String date)}: Converts an ISO-8601 formatted string to a {@code LocalDate} object.
 * Returns {@code null} if the input is {@code null} or blank.
 */
public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

  @Override
  public String marshal(LocalDate date) {
    return (date != null) ? date.format(FORMATTER) : null;
  }

  @Override
  public LocalDate unmarshal(String date) {
    return (date != null && !date.isBlank()) ? LocalDate.parse(date, FORMATTER) : null;
  }
}
