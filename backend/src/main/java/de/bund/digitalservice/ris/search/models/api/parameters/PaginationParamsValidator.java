package de.bund.digitalservice.ris.search.models.api.parameters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PaginationParamsValidator
    implements ConstraintValidator<ValidatePaginationParams, PaginationParams> {

  @Override
  public boolean isValid(PaginationParams obj, ConstraintValidatorContext context) {
    boolean resultWindowsIsValid = (obj.pageIndex * obj.size) + obj.size <= 10000;
    if (!resultWindowsIsValid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "PageIndex out of Bounds. Result window must not exceed 10000")
          .addConstraintViolation();
    }
    return resultWindowsIsValid;
  }
}
