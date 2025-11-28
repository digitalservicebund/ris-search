package de.bund.digitalservice.ris;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.bund.digitalservice.ris.search.schema.AbstractDocumentSchema;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSearchSchema;
import de.bund.digitalservice.ris.search.schema.CaseLawSearchSchema;
import de.bund.digitalservice.ris.search.schema.CollectionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSearchSchema;
import de.bund.digitalservice.ris.search.schema.LiteratureSearchSchema;
import de.bund.digitalservice.ris.search.schema.SearchMemberSchema;
import java.util.List;
import java.util.Objects;

/** Utility class for JSON parsing and data extraction in tests. */
public class TestJsonUtils {

  private static final TypeReference<CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>>>
      typeRef = new TypeReference<>() {};
  private static final ObjectMapper objectMapper = createObjectMapper();

  private static ObjectMapper createObjectMapper() {
    ObjectMapper result = new ObjectMapper();
    result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    result.registerModule(new JavaTimeModule());
    return result;
  }

  /**
   * Parses a JSON string into a CollectionSchema containing SearchMemberSchema of
   * AbstractDocumentSchema.
   *
   * @param jsonString the JSON string to parse
   * @return the parsed CollectionSchema
   */
  public static CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>> parseJsonResult(
      String jsonString) {
    try {
      return objectMapper.readValue(jsonString, typeRef);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<String> getDates(
      CollectionSchema<SearchMemberSchema<AbstractDocumentSchema>> response) {
    return response.member().stream().map(e -> getDate(e.item())).toList();
  }

  private static String getDate(AbstractDocumentSchema entity) {
    return switch (entity) {
      case CaseLawSearchSchema c -> c.decisionDate().toString();
      case LiteratureSearchSchema l -> l.firstPublicationDate().toString();
      case LegislationWorkSearchSchema n ->
          Objects.requireNonNull(n.workExample()).temporalCoverage().substring(0, 10);
      case AdministrativeDirectiveSearchSchema n ->
          Objects.requireNonNull(n.entryIntoForceDate()).toString();
    };
  }
}
