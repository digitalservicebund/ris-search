package de.bund.digitalservice.ris.search.models.sitemap;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
