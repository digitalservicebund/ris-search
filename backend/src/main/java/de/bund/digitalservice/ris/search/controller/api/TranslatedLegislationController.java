package de.bund.digitalservice.ris.search.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.TranslatedLegislationsJson;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.schema.LegislationTranslationSchema;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslatedLegislationController {

  private final PortalBucket portalBucket;

  public TranslatedLegislationController(PortalBucket portalBucket) {
    this.portalBucket = portalBucket;
  }

  @GetMapping(
      value = ApiConfig.Paths.LEGISLATION_TRANSLATION,
      produces = MediaType.APPLICATION_JSON_VALUE)
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

  @GetMapping(
      value = ApiConfig.Paths.LEGISLATION_TRANSLATION + "/{filename}",
      produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getTranslationHTML(@PathVariable String filename) {
    try {
      Optional<String> translationHtmlString =
          portalBucket.getFileAsString("translations/%s".formatted(filename));
      if (translationHtmlString.isPresent()) {
        return ResponseEntity.ok(translationHtmlString.get());
      }
      return ResponseEntity.notFound().build();
    } catch (ObjectStoreServiceException exception) {
      return ResponseEntity.notFound().build();
    }
  }

  private List<TranslatedLegislationsJson> readDataFromJson(String abbreviation)
      throws IOException, ObjectStoreServiceException {
    ObjectMapper mapper = new ObjectMapper();
    Optional<String> dataJson = portalBucket.getFileAsString("translations/data.json");
    if (dataJson.isPresent()) {
      List<TranslatedLegislationsJson> resultList =
          mapper.readValue(
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
