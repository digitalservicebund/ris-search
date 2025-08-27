package de.bund.digitalservice.ris.search.models.api.parameters;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PaginationParamsValidator.class)
public @interface ValidatePaginationParams {
  String message() default "invalid pagination parameters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
