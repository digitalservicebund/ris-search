package de.bund.digitalservice.ris.search.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.TranslatedLegislationsJson;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.schema.LegislationTranslationSchema;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing translated legislations. */
@RestController
public class TranslatedLegislationController {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final PortalBucket portalBucket;

  public TranslatedLegislationController(PortalBucket portalBucket) {
    this.portalBucket = portalBucket;
  }

  /**
   * Retrieves a list of legislation translations, optionally filtered by an identifier. The
   * translations are returned in a standardized schema format.
   *
   * @param id an optional identifier to filter the legislation translations; if null or not
   *     provided, all translations are returned
   * @return a list of {@code LegislationTranslationSchema} objects representing the filtered or
   *     complete legislation translations
   * @throws IOException if there is an error reading data from the JSON source
   * @throws ObjectStoreServiceException if there is an issue accessing the object storage service
   */
  @GetMapping(
      value = ApiConfig.Paths.LEGISLATION_TRANSLATION,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Hidden
  public List<LegislationTranslationSchema> listAndFilter(
      @Parameter @RequestParam(required = false) String id)
      throws IOException, ObjectStoreServiceException {
    return readDataFromJson(id).stream()
        .map(
            item ->
                LegislationTranslationSchema.builder()
                    .id(item.abbreviation())
                    .name(item.name())
                    .inLanguage("en")
                    .translator(item.translator())
                    .translationOfWork(item.germanName())
                    .about(item.version())
                    .filename(item.filename())
                    .build())
        .toList();
  }

  /**
   * Fetches the HTML representation of a translated legislation file based on the provided
   * filename.
   *
   * @param filename the name of the file to fetch the translation for
   * @return a ResponseEntity containing the HTML content of the translation if found, or an
   *     appropriate error message and HTTP status if the file is not found or if there's an error
   *     accessing the object store
   */
  @GetMapping(
      value = ApiConfig.Paths.LEGISLATION_TRANSLATION + "/{filename}",
      produces = MediaType.TEXT_HTML_VALUE)
  @Hidden
  public ResponseEntity<String> getTranslationHTML(@PathVariable String filename) {
    try {
      Optional<String> translationHtmlString =
          portalBucket.getFileAsString("translations/%s".formatted(filename));
      if (translationHtmlString.isPresent()) {
        return ResponseEntity.ok(translationHtmlString.get());
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Translation not found.");
    } catch (ObjectStoreServiceException exception) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error accessing the object store.");
    }
  }

  private List<TranslatedLegislationsJson> readDataFromJson(String abbreviation)
      throws IOException, ObjectStoreServiceException {
    Optional<String> dataJson = portalBucket.getFileAsString("translations/data.json");
    if (dataJson.isPresent()) {
      List<TranslatedLegislationsJson> resultList =
          OBJECT_MAPPER.readValue(
              dataJson.get(), new TypeReference<List<TranslatedLegislationsJson>>() {});
      if (abbreviation == null || abbreviation.isEmpty()) {
        return resultList;
      }
      return resultList.stream()
          .filter(item -> abbreviation.equalsIgnoreCase(item.abbreviation()))
          .toList();
    } else {
      return Collections.emptyList();
    }
  }
}
