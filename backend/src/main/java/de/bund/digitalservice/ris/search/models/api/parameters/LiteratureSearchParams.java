package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the parameters for searching literature documents.
 *
 * <p>The following parameters can be utilized to filter and search for relevant literature:
 *
 * <p>- `documentNumber`: Used to specify a unique identifier for the literature document being
 * searched.
 *
 * <p>- `yearOfPublication`: Filters the results by one or more publication years of the literature.
 *
 * <p>- `documentType`: Filters the results by one or more types of literature documents.
 *
 * <p>- `author`: Filters the results by one or more authors of the literature.
 *
 * <p>- `collaborator`: Filters the results by one or more collaborators involved in the literature.
 */
@Getter
@Setter
public class LiteratureSearchParams {

  @Schema String documentNumber;

  @Schema String[] yearOfPublication;

  @Schema String[] documentType;

  @Schema String[] author;

  @Schema String[] collaborator;
}
