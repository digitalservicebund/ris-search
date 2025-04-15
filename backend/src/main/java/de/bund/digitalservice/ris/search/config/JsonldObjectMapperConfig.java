package de.bund.digitalservice.ris.search.config;

import static de.bund.digitalservice.ris.search.utils.DateUtils.DATE_FORMATTER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import java.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * A configuration bean that provides an instance of {@link
 * com.fasterxml.jackson.databind.ObjectMapper}, which can be used to serialize and deserialize JSON
 * data. It adds support for JSON-LD @id and @type annotations. It also registers a custom date
 * format.
 */
@Configuration
public class JsonldObjectMapperConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JsonldModule());

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    // use format yyyy-MM-dd to serialize LocalDates
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
    objectMapper.registerModule(javaTimeModule);

    return objectMapper;
  }
}
