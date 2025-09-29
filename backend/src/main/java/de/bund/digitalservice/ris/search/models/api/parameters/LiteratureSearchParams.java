package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiteratureSearchParams {

  @Schema String documentNumber;

  @Schema String[] yearOfPublication;

  @Schema String[] documentType;

  @Schema String[] author;

  @Schema String[] collaborator;
}
