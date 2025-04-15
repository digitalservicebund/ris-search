package de.bund.digitalservice.ris.search.utils;

import static de.bund.digitalservice.ris.search.utils.DateUtils.DATE_FORMATTER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import java.time.LocalDate;

public class JsonLdUtils {

  private JsonLdUtils() {}

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JsonldModule());

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    // use format yyyy-MM-dd to serialize LocalDates
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
    objectMapper.registerModule(javaTimeModule);
  }

  public static String writeJsonLdString(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
