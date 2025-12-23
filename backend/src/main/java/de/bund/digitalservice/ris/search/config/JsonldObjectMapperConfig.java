package de.bund.digitalservice.ris.search.config;

import static de.bund.digitalservice.ris.search.utils.DateUtils.DATE_FORMATTER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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

  /**
   * Provides a configured {@link ObjectMapper} bean for JSON serialization and deserialization.
   *
   * <p>This method customizes the {@link ObjectMapper} by registering additional modules: - {@link
   * JsonldModule} to enable support for JSON-LD annotations (@id, @type). - {@link JavaTimeModule}
   * for better handling of Java 8 date/time types, with a custom serializer for {@link LocalDate}
   * to format dates using the pattern "yyyy-MM-dd".
   *
   * @return A configured instance of {@link ObjectMapper}, suitable for handling JSON-LD data and
   *     Java 8 date/time types with custom date formatting.
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    var objectMapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    // use format yyyy-MM-dd to serialize LocalDates
    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
    objectMapper.registerModule(javaTimeModule);

    return objectMapper;
  }
}
