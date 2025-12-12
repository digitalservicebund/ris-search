package de.bund.digitalservice.ris.search.unit.models.api.parameters;

import de.bund.digitalservice.ris.search.models.api.parameters.PaginationParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PaginationParamsValidatorTest {

  private static Validator validator;

  @BeforeAll
  static void setUpClass() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  private static Stream<Arguments> provideInvalidPaginationTestArguments() {
    PaginationParams invalidResultWindow = new PaginationParams();
    invalidResultWindow.setPageIndex(100);
    invalidResultWindow.setSize(100);

    PaginationParams invalidPageIndex = new PaginationParams();
    invalidPageIndex.setSize(100);
    invalidPageIndex.setPageIndex(-1);

    PaginationParams sizeTooBig = new PaginationParams();
    sizeTooBig.setSize(301);
    sizeTooBig.setPageIndex(1);

    PaginationParams sizeTooSmall = new PaginationParams();
    sizeTooSmall.setSize(0);
    sizeTooSmall.setPageIndex(1);

    return Stream.of(
        Arguments.of(
            invalidResultWindow, "PageIndex out of Bounds. Result window must not exceed 10000"),
        Arguments.of(invalidPageIndex, "pageIndex must be at least 0"),
        Arguments.of(sizeTooBig, "size must not exceed 300"),
        Arguments.of(sizeTooSmall, "size must be at least 1"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidPaginationTestArguments")
  void itDetectsViolationsAndSetsTheCorrectMessage(
      PaginationParams params, String expectedMessage) {
    Set<ConstraintViolation<PaginationParams>> constraints = validator.validate(params);

    Assertions.assertEquals(1, constraints.size());
    ConstraintViolation<PaginationParams> constraint = constraints.iterator().next();

    Assertions.assertEquals(expectedMessage, constraint.getMessage());
  }

  @Test
  void itLetsCorrectParametersPass() {
    PaginationParams params = new PaginationParams();
    params.setSize(100);
    params.setPageIndex(99);
    Set<ConstraintViolation<PaginationParams>> constraints = validator.validate(params);

    Assertions.assertEquals(0, constraints.size());
  }
}
