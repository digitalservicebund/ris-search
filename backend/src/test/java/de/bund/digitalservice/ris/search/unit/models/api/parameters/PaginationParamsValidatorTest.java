package de.bund.digitalservice.ris.search.unit.models.api.parameters;

import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PaginationParamsValidatorTest {

  private static Validator validator;

  @BeforeAll
  static void setUpClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void itDetectsViolationWhenResultWindowIsExceeded() {
    PaginationParams params = new PaginationParams();
    params.setSize(100);
    params.setPageIndex(100);
    Set<ConstraintViolation<PaginationParams>> constraints = validator.validate(params);

    Assertions.assertEquals(1, constraints.size());
    ConstraintViolation<PaginationParams> constraint = constraints.iterator().next();

    Assertions.assertEquals(
        "PageIndex out of Bounds. Result window must not exceed 10000", constraint.getMessage());
  }

  @Test
  void itLetsCorrectResultWindowPass() {
    PaginationParams params = new PaginationParams();
    params.setSize(100);
    params.setPageIndex(99);
    Set<ConstraintViolation<PaginationParams>> constraints = validator.validate(params);

    Assertions.assertEquals(0, constraints.size());
  }
}
