package de.bund.digitalservice.ris.search.exception;

import de.bund.digitalservice.ris.search.models.errors.CustomError;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * This class is used to define a custom exception with a well-defined structure - which includes a
 * list of errors ({@link CustomError}) .
 */
@Getter
public class CustomValidationException extends Exception {
  private final List<CustomError> errors;

  public CustomValidationException(List<CustomError> errors) {
    this.errors = new ArrayList<>(errors);
  }

  public CustomValidationException(CustomError error) {
    this.errors = List.of(error);
  }
}
