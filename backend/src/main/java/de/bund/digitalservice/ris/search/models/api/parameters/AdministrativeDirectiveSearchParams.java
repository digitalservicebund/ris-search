package de.bund.digitalservice.ris.search.models.api.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the search parameters for administrative directives.
 *
 * <p>This class allows specifying criteria for filtering or querying administrative directive data.
 * The `documentNumber` field is utilized for searching specific documents based on their unique
 * identification number.
 */
@Getter
@Setter
public class AdministrativeDirectiveSearchParams {

  @Schema String documentNumber;
}
